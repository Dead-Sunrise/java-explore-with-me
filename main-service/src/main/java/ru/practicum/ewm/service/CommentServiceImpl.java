package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.comment.CommentDto;
import ru.practicum.ewm.dto.comment.NewCommentDto;
import ru.practicum.ewm.enums.CommentStatus;
import ru.practicum.ewm.enums.EventState;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.mapper.CommentMapper;
import ru.practicum.ewm.model.Comment;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.repository.CommentRepository;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final CommentMapper commentMapper;

    @Override
    public CommentDto privateCreateComment(Long userId, Long eventId, NewCommentDto newCommentDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id: " + userId + " не существует."));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с id: " + eventId + " не существует."));
        if (event.getState() != EventState.PUBLISHED) {
            throw new ConflictException("Событие с id: " + eventId + " не опубликовано, комментирование не возможно.");
        }
        Comment comment = commentMapper.newCommentDtoToComment(newCommentDto, user, event);
        return commentMapper.commentToCommentDto(commentRepository.save(comment));
    }

    @Override
    public CommentDto privateUpdateComment(Long userId, Long commentId, NewCommentDto newCommentDto) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Комментарий c id: " + commentId + " не существует."));
        if (!comment.getAuthor().getId().equals(userId)) {
            throw new NotFoundException("Пользователь с id: " + userId + " не существует, либо не является автором комментария.");
        }
        comment.setText(newCommentDto.getText());
        comment.setStatus(CommentStatus.PENDING);
        return commentMapper.commentToCommentDto(commentRepository.save(comment));
    }

    @Override
    public void privateDeleteComment(Long userId, Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Комментарий c id: " + commentId + " не существует."));
        if (!comment.getAuthor().getId().equals(userId)) {
            throw new NotFoundException("Пользователь с id: " + userId + " не существует, либо не является автором комментария.");
        }
        commentRepository.delete(comment);
    }

    @Override
    public List<CommentDto> adminGetCommentsByStatus(CommentStatus status, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        return commentRepository.findByStatus(status, pageable).stream()
                .map(commentMapper::commentToCommentDto)
                .toList();
    }

    @Override
    public CommentDto adminUpdateCommentStatus(Long commentId, CommentStatus status) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Комментарий c id: " + commentId + " не существует."));
        if (comment.getStatus() != CommentStatus.PENDING) {
            throw new ConflictException("Комментарий уже обработан.");
        }
        if (status == CommentStatus.PENDING) {
            throw new ConflictException("Невозможно установить статус PENDING.");
        }
        comment.setStatus(status);
        return commentMapper.commentToCommentDto(commentRepository.save(comment));
    }

    @Override
    public List<CommentDto> publicGetEventComments(Long eventId, int from, int size) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с id: " + eventId + " не существует."));
        Pageable pageable = PageRequest.of(from / size, size);
        return commentRepository.findByEventIdAndStatus(eventId, CommentStatus.PUBLISHED, pageable).stream()
                .map(commentMapper::commentToCommentDto)
                .toList();
    }

    @Override
    public CommentDto publicGetCommentById(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Комментарий c id: " + commentId + " не существует."));
        if (comment.getStatus() != CommentStatus.PUBLISHED) {
            throw new ConflictException("Комментарий не опубликован, просмотр недоступен.");
        }
        return commentMapper.commentToCommentDto(comment);
    }
}

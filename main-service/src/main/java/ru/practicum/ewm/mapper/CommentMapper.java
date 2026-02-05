package ru.practicum.ewm.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.ewm.dto.comment.CommentDto;
import ru.practicum.ewm.dto.comment.NewCommentDto;
import ru.practicum.ewm.enums.CommentStatus;
import ru.practicum.ewm.model.Comment;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.User;

import java.time.LocalDateTime;

@Component
public class CommentMapper {
    public CommentDto commentToCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .author(UserMapper.userToUserShortDto(comment.getAuthor()))
                .eventId(comment.getEvent().getId())
                .created(comment.getCreated())
                .status(comment.getStatus())
                .build();
    }

    public Comment newCommentDtoToComment(NewCommentDto newCommentDto, User user, Event event) {
        return Comment.builder()
                .text(newCommentDto.getText())
                .author(user)
                .event(event)
                .created(LocalDateTime.now())
                .status(CommentStatus.PENDING)
                .build();
    }
}

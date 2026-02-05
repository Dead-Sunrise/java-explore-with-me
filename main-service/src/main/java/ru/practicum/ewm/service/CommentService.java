package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.comment.CommentDto;
import ru.practicum.ewm.dto.comment.NewCommentDto;
import ru.practicum.ewm.enums.CommentStatus;

import java.util.List;

public interface CommentService {
    CommentDto privateCreateComment(Long userId, Long eventId, NewCommentDto newCommentDto);

    CommentDto privateUpdateComment(Long userId, Long commentId, NewCommentDto newCommentDto);

    void privateDeleteComment(Long userId, Long commentId);

    List<CommentDto> adminGetCommentsByStatus(CommentStatus status, int from, int size);

    CommentDto adminUpdateCommentStatus(Long commentId, CommentStatus status);

    List<CommentDto> publicGetEventComments(Long eventId, int from, int size);

    CommentDto publicGetCommentById(Long commentId);
}

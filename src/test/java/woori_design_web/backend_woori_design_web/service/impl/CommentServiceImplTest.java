package woori_design_web.backend_woori_design_web.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import woori_design_web.backend_woori_design_web.entity.Comment;
import woori_design_web.backend_woori_design_web.entity.User;
import woori_design_web.backend_woori_design_web.repository.CommentRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentServiceImpl commentService;

    @BeforeEach
    void setUp() {
    }

    @Test
    @DisplayName("댓글 등록 테스트")
    void testRegisterComment() {
        User alice = User.builder().id(1L).name("Alice").build();
        Comment newComment = Comment.builder()
                .user(alice)
                .postId(1L)
                .content("First comment")
                .build();

        Comment savedComment = Comment.builder()
                .id(1L)
                .user(alice)
                .postId(1L)
                .content("First comment")
                .createdAt(LocalDateTime.of(2025, 2, 12, 10, 0))
                .updatedAt(LocalDateTime.of(2025, 2, 12, 10, 0))
                .build();

        when(commentRepository.save(any(Comment.class))).thenReturn(savedComment);

        Long commentId = commentService.registerComment(newComment);
        assertEquals(1L, commentId);
    }

    @Test
    @DisplayName("존재하는 댓글 조회 테스트")
    void testGetExistingComment() {
        User alice = User.builder().id(1L).name("Alice").build();
        Comment savedComment = Comment.builder()
                .id(1L)
                .user(alice)
                .postId(1L)
                .content("First comment")
                .createdAt(LocalDateTime.of(2025, 2, 12, 10, 0))
                .updatedAt(LocalDateTime.of(2025, 2, 12, 10, 0))
                .build();



        when(commentRepository.findById(1L)).thenReturn(Optional.of(savedComment));

        Comment retrievedComment = commentService.getComment(1L);
        assertNotNull(retrievedComment);
        assertEquals("First comment", retrievedComment.getContent());
    }

    @Test
    @DisplayName("존재하지 않는 댓글 조회 테스트")
    void testGetNonExistentComment() {
        when(commentRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> commentService.getComment(999L));
    }

    @Test
    @DisplayName("댓글 개수 조회 테스트")
    void testGetCommentCount() {
        when(commentRepository.count()).thenReturn(1L);
        Long commentCount = commentService.getCommentCount();
        assertEquals(1L, commentCount);
    }
}

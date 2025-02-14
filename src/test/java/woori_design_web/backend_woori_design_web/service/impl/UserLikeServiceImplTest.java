package woori_design_web.backend_woori_design_web.service.impl;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import woori_design_web.backend_woori_design_web.dto.UserLikeDto;
import woori_design_web.backend_woori_design_web.entity.User;
import woori_design_web.backend_woori_design_web.entity.UserLike;
import woori_design_web.backend_woori_design_web.repository.UserLikeRepository;

import java.io.Console;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserLikeServiceImplTest {

    @Mock
    private UserLikeRepository userLikeRepository;

    @InjectMocks
    private UserLikeServiceImpl userLikeService;

    private User user;
    private UserLike userLike;
    private UserLikeDto userLikeDto;

    @BeforeAll
    static void beforeAll() {
        System.out.println("🛠 테스트 시작!");
    }

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).build();
        userLike = UserLike.builder()
                .postId(100L)
                .createdAt(LocalDateTime.now())
                .build();
        userLikeDto = new UserLikeDto(userLike);
    }

    @Test
    @DisplayName("좋아요 등록 테스트")
    void testAddLike() {
        when(userLikeRepository.save(any(UserLike.class))).thenReturn(userLike);

        UserLikeDto savedLikeDto = userLikeService.addLike(1L, 100L);

        assertNotNull(savedLikeDto);
        assertEquals(100L, savedLikeDto.getPostId());

        verify(userLikeRepository, times(1)).save(any(UserLike.class));
    }

    @Test
    @DisplayName("좋아요 삭제 테스트")
    void testRemoveLike() {
        when(userLikeRepository.findByUserIdAndPostId(1L, 100L)).thenReturn(Optional.of(userLike));
        doNothing().when(userLikeRepository).delete(userLike);

        assertDoesNotThrow(() -> userLikeService.removeLike(1L, 100L));
        verify(userLikeRepository, times(1)).delete(userLike);
    }

    @Test
    @DisplayName("특정 컴포넌트(postId)의 좋아요 개수 조회 테스트")
    void testGetLikeCountByPostId() {
        when(userLikeRepository.existsByPostId(100L)).thenReturn(true);
        when(userLikeRepository.countLikesByPostId(100L)).thenReturn(3L);

        Long likeCount = userLikeService.getLikeCountByPostId(100L);

        assertNotNull(likeCount);
        assertEquals(3L, likeCount);
    }

    @Test
    @DisplayName("존재하지 않는 postId로 좋아요 개수 조회 시, 예외 처리 테스트")
    void testGetLikeCountByPostId_NotFound() {
        when(userLikeRepository.existsByPostId(999L)).thenReturn(false);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            userLikeService.getLikeCountByPostId(999L);
        });

        assertEquals("해당 postId에 대한 좋아요 기록이 없습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("존재하지 않는 좋아요 삭제 시, 예외 처리 테스트")
    void testRemoveLikeWithInvalidPost() {
        when(userLikeRepository.findByUserIdAndPostId(1L, 999L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userLikeService.removeLike(1L, 999L);
        });

        assertEquals("해당 좋아요가 존재하지 않습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("유효하지 않은 userId로 좋아요 등록 시, 예외 처리 테스트")
    void testAddLikeWithInvalidUser() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userLikeService.addLike(null, 100L);
        });

        assertEquals("❌ User ID 또는 Post ID는 null일 수 없습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("같은 userId가 동일한 postId에 대해 중복 좋아요 시도 시 예외 처리 테스트")
    void testAddLikeWithDuplicateUser() {
        when(userLikeRepository.findByUserIdAndPostId(1L, 100L)).thenReturn(Optional.of(userLike));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            userLikeService.addLike(1L, 100L);
        });

        assertEquals("🚀 중복 좋아요 등록 시도 - userId: 1, postId: 100", exception.getMessage());

        verify(userLikeRepository, never()).save(any(UserLike.class));
    }

}

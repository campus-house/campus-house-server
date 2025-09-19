package com.example.campus_house.service;

import com.example.campus_house.entity.Property;
import com.example.campus_house.entity.PropertyQnA;
import com.example.campus_house.entity.User;
import com.example.campus_house.repository.PropertyQnARepository;
import com.example.campus_house.repository.PropertyRepository;
import com.example.campus_house.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PropertyQnAService {
    
    private final PropertyQnARepository propertyQnARepository;
    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;
    
    // 특정 매물의 Q&A 조회 (질문만)
    @Cacheable(value = "qnas", key = "#propertyId + '_questions_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<PropertyQnA> getQuestionsByPropertyId(Long propertyId, Pageable pageable) {
        return propertyQnARepository.findQuestionsByPropertyId(propertyId, pageable);
    }
    
    // 특정 매물의 Q&A 조회 (전체)
    public Page<PropertyQnA> getQnAsByPropertyId(Long propertyId, Pageable pageable) {
        return propertyQnARepository.findByPropertyIdAndStatusOrderByCreatedAtDesc(
                propertyId, PropertyQnA.QnAStatus.ACTIVE, pageable);
    }
    
    // 특정 질문의 답변 조회
    public List<PropertyQnA> getAnswersByParentId(Long parentId) {
        return propertyQnARepository.findAnswersByParentId(parentId);
    }
    
    // 특정 매물의 Q&A 수 조회
    public long getQnACountByPropertyId(Long propertyId) {
        return propertyQnARepository.countByPropertyIdAndStatus(propertyId, PropertyQnA.QnAStatus.ACTIVE);
    }
    
    // 특정 사용자의 Q&A 조회
    public Page<PropertyQnA> getQnAsByUserId(Long userId, Pageable pageable) {
        return propertyQnARepository.findByUserIdAndStatusOrderByCreatedAtDesc(
                userId, PropertyQnA.QnAStatus.ACTIVE, pageable);
    }
    
    // 답변이 없는 질문 조회
    public Page<PropertyQnA> getUnansweredQuestionsByPropertyId(Long propertyId, Pageable pageable) {
        return propertyQnARepository.findUnansweredQuestionsByPropertyId(propertyId, pageable);
    }
    
    // 인기 Q&A 조회
    public Page<PropertyQnA> getPopularQnAs(Pageable pageable) {
        return propertyQnARepository.findPopularQnAs(pageable);
    }
    
    // 질문 작성
    @Transactional
    public PropertyQnA createQuestion(Long propertyId, Long userId, String content, String imageUrl) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("매물을 찾을 수 없습니다."));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        
        PropertyQnA question = PropertyQnA.builder()
                .property(property)
                .user(user)
                .content(content)
                .imageUrl(imageUrl)
                .type(PropertyQnA.QnAType.QUESTION)
                .status(PropertyQnA.QnAStatus.ACTIVE)
                .likeCount(0)
                .build();
        
        return propertyQnARepository.save(question);
    }
    
    // 답변 작성
    @Transactional
    public PropertyQnA createAnswer(Long propertyId, Long userId, Long parentId, String content, String imageUrl) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("매물을 찾을 수 없습니다."));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        
        PropertyQnA parent = propertyQnARepository.findById(parentId)
                .orElseThrow(() -> new RuntimeException("질문을 찾을 수 없습니다."));
        
        PropertyQnA answer = PropertyQnA.builder()
                .property(property)
                .user(user)
                .parent(parent)
                .content(content)
                .imageUrl(imageUrl)
                .type(PropertyQnA.QnAType.ANSWER)
                .status(PropertyQnA.QnAStatus.ACTIVE)
                .likeCount(0)
                .build();
        
        return propertyQnARepository.save(answer);
    }
    
    // Q&A 수정
    @Transactional
    public PropertyQnA updateQnA(Long qnaId, Long userId, String content, String imageUrl) {
        PropertyQnA qna = propertyQnARepository.findById(qnaId)
                .orElseThrow(() -> new RuntimeException("Q&A를 찾을 수 없습니다."));
        
        // 작성자만 수정 가능
        if (!qna.getUser().getId().equals(userId)) {
            throw new RuntimeException("Q&A를 수정할 권한이 없습니다.");
        }
        
        qna.setContent(content);
        qna.setImageUrl(imageUrl);
        qna.setUpdatedAt(LocalDateTime.now());
        
        return propertyQnARepository.save(qna);
    }
    
    // Q&A 삭제
    @Transactional
    public void deleteQnA(Long qnaId, Long userId) {
        PropertyQnA qna = propertyQnARepository.findById(qnaId)
                .orElseThrow(() -> new RuntimeException("Q&A를 찾을 수 없습니다."));
        
        // 작성자만 삭제 가능
        if (!qna.getUser().getId().equals(userId)) {
            throw new RuntimeException("Q&A를 삭제할 권한이 없습니다.");
        }
        
        qna.setStatus(PropertyQnA.QnAStatus.DELETED);
        propertyQnARepository.save(qna);
    }
    
    // Q&A 좋아요 수 업데이트
    @Transactional
    public void updateQnALikeCount(Long qnaId) {
        PropertyQnA qna = propertyQnARepository.findById(qnaId)
                .orElseThrow(() -> new RuntimeException("Q&A를 찾을 수 없습니다."));
        
        qna.setLikeCount(qna.getLikes().size());
        propertyQnARepository.save(qna);
    }
}

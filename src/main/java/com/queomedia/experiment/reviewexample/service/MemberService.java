package com.queomedia.experiment.reviewexample.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.queomedia.experiment.reviewexample.domain.Member;
import com.queomedia.experiment.reviewexample.exception.DuplicateResourceException;
import com.queomedia.experiment.reviewexample.exception.ResourceNotFoundException;
import com.queomedia.experiment.reviewexample.repository.MemberRepository;

@Service
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public Member createMember(Member member) {
        memberRepository.findByEmail(member.getEmail()).ifPresent(existing -> {
            throw new DuplicateResourceException("A member with this email already exists");
        });
        member.setMemberNumber(generateMemberNumber());
        return memberRepository.save(member);
    }

    @Transactional(readOnly = true)
    public Member getMemberById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Member> searchByName(String name) {
        return memberRepository.findByNameContainingIgnoreCase(name);
    }

    private String generateMemberNumber() {
        return "MEM-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}

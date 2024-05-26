package firstskin.firstskin.user.service;

import firstskin.firstskin.dianosis.DiagnosisRepository;
import firstskin.firstskin.dianosis.api.request.DiagnosisDto;
import firstskin.firstskin.dianosis.domain.Diagnosis;
import firstskin.firstskin.member.domain.Member;
import firstskin.firstskin.member.repository.MemberRepository;
import firstskin.firstskin.skin.Skin;
import firstskin.firstskin.user.api.dto.SelfDiagnosisDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

;

@Service
public class SelfDiagnosisService {

    private final DiagnosisRepository diagnosisRepository;


    @Autowired
    public SelfDiagnosisService(DiagnosisRepository diagnosisRepository, MemberRepository memberRepository) {
        this.diagnosisRepository = diagnosisRepository;


    }

    public Optional<SelfDiagnosisDto> getDiagnosisByDate(Optional<Member> member, LocalDate date) {
        if (member.isEmpty()) {
            return Optional.empty();
        }
        LocalDateTime startDate = date.atStartOfDay();
        LocalDateTime endDate = startDate.plusDays(1).minusNanos(1);
        Optional<Diagnosis> diagnosis = diagnosisRepository.findTopByMemberAndCreatedDateBetweenOrderByCreatedDateDesc(member.get(), startDate, endDate);
        return diagnosis.map(this::SelfConvertToDto);
    }
    private SelfDiagnosisDto SelfConvertToDto(Diagnosis diagnosis) {
        Skin skin = diagnosis.getSkin();
        return new SelfDiagnosisDto(skin.getKind().getDescription(), skin.getResult(), diagnosis.getSkinPictureUrl());
    }
}
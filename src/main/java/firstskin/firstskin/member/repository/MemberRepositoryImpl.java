package firstskin.firstskin.member.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import firstskin.firstskin.admin.api.dto.response.MemberResponse;
import firstskin.firstskin.dianosis.api.response.PersonalResult;
import firstskin.firstskin.dianosis.domain.QDiagnosis;
import firstskin.firstskin.member.domain.Member;
import firstskin.firstskin.skin.Kind;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static firstskin.firstskin.dianosis.domain.QDiagnosis.diagnosis;
import static firstskin.firstskin.member.domain.QMember.member;
import static firstskin.firstskin.skin.QSkin.skin;

@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<MemberResponse> getMembers(Pageable pageable) {
        List<MemberResponse> fetch = queryFactory
                .select(Projections.constructor(
                        MemberResponse.class,
                        member.memberId.as("memberId"),
                        member.userId.as("userId"),
                        member.profileUrl.as("profile"),
                        member.createdDate.as("createdDate"),
                        member.nickname.as("nickname"),
                        selectKindOfSkin(Kind.TYPE),
                        selectKindOfSkin(Kind.TROUBLE),
                        selectKindOfSkin(Kind.PERSONAL_COLOR)
                ))
                .from(member)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long count = queryFactory
                .select(member.count())
                .from(member)
                .fetchOne();

        return new PageImpl<>(fetch, pageable, count == null ? 0 : count);
    }

    @Override
    public PersonalResult getPersonalResults(Member member) {

        List<PersonalResult> fetch = queryFactory
                .select(Projections.constructor(
                        PersonalResult.class,
                        selectKindOfPersonalSkin(Kind.TYPE, member),
                        selectKindOfPersonalSkin(Kind.PERSONAL_COLOR, member),
                        selectKindOfPersonalSkin(Kind.TROUBLE, member)
                )).from(diagnosis)
                .where(diagnosis.member.eq(member))
                .fetch();

        System.out.println("fetch = " + fetch);

        return fetch.get(0);
    }


    private JPQLQuery<String> selectKindOfSkin(Kind kind) {
        QDiagnosis subDiagnosis = new QDiagnosis("subDiagnosis");

        return JPAExpressions
                .select(skin.result)
                .from(diagnosis)
                .join(diagnosis.skin, skin)
                .where(diagnosis.member.eq(member)
                        .and(skin.kind.eq(kind))
                        .and(diagnosis.createdDate.eq(
                                JPAExpressions
                                        .select(subDiagnosis.createdDate.max())
                                        .from(subDiagnosis)
                                        .where(subDiagnosis.member.eq(member)
                                                .and(subDiagnosis.skin.kind.eq(kind)))
                        )));
    }

    private JPQLQuery<String> selectKindOfPersonalSkin(Kind kind, Member member){
        QDiagnosis subDiagnosis = new QDiagnosis("subDiagnosis");

        return JPAExpressions
                .select(skin.result)
                .from(diagnosis)
                .join(diagnosis.skin, skin)
                .where(diagnosis.member.eq(member)
                        .and(skin.kind.eq(kind))
                        .and(diagnosis.createdDate.eq(
                                JPAExpressions
                                        .select(subDiagnosis.createdDate.max())
                                        .from(subDiagnosis)
                                        .where(subDiagnosis.member.eq(member)
                                                .and(subDiagnosis.skin.kind.eq(kind)))
                        )));
    }
}

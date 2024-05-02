package firstskin.firstskin.member.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import firstskin.firstskin.admin.api.dto.response.MemberResponse;
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

    private static JPQLQuery<String> selectKindOfSkin(Kind type) {
        return JPAExpressions
                .select(skin.result)
                .from(diagnosis)
                .join(diagnosis.skin, skin)
                .where(diagnosis.member.eq(member).and(skin.kind.eq(type)))
                .orderBy(diagnosis.createdDate.desc())
                .limit(1);
    }
}

package cart.authority;


import cart.controller.dto.request.MemberId;
import cart.dao.MemberDao;
import cart.entity.MemberEntity;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class AuthorityMemberArgumentResolver implements HandlerMethodArgumentResolver {

    private static final String BASIC_TYPE = "Basic ";
    private final MemberDao memberDao;

    public AuthorityMemberArgumentResolver(final MemberDao memberDao) {
        this.memberDao = memberDao;
    }

    @Override
    public boolean supportsParameter(final MethodParameter parameter) {
        boolean hasAuthorityAnnotation = parameter.hasParameterAnnotation(Authority.class);
        boolean hasMemberType = MemberId.class.isAssignableFrom(parameter.getParameterType());

        return hasMemberType && hasAuthorityAnnotation;
    }

    @Override
    public Object resolveArgument(final MethodParameter parameter,
                                  final ModelAndViewContainer mavContainer,
                                  final NativeWebRequest webRequest,
                                  final WebDataBinderFactory binderFactory
    ) {
        final String token = getBasicToken(webRequest);
        final String[] authInformation = getAuthInformation(token);
        final String email = authInformation[0];
        final String password = authInformation[1];

        final MemberEntity memberEntity = new MemberEntity(email, password);

        final Long memberId = memberDao.selectMemberId(memberEntity)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일로 가입된 회원이 없습니다"));

        return new MemberId(memberId);
    }

    private String getBasicToken(final NativeWebRequest webRequest) {
        final String token = webRequest.getHeader(HttpHeaders.AUTHORIZATION);
        if (!StringUtils.hasText(token)) {
            throw new IllegalArgumentException("Authorization 헤더가 없습니다.");
        }
        if (!token.startsWith(BASIC_TYPE)) {
            throw new IllegalArgumentException("토큰의 형식은 Basic 이어야 합니다.");
        }
        return token.replaceFirst(BASIC_TYPE, "");
    }

    private String[] getAuthInformation(final String token) {
        final String emailAndPassword = new String(Base64Utils.decodeFromString(token));
        return emailAndPassword.split(":");
    }
}
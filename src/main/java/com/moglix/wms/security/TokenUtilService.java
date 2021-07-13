package com.moglix.wms.security;

import com.moglix.ems.entities.ApplicationUser;
import com.moglix.ems.repository.UserRepository;
import com.moglix.wms.constants.Constants;
import com.moglix.wms.util.RandomStringUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.Optional;


@Transactional
@Service
public class TokenUtilService {

    private UserRepository userRepository;
    private String secret = "SsvUBb&\\8K$#B_w;cfz-7hS_)w;4He4L2VkjKUSP\"wB!;?2d#5[%=FQFt6T3ujjvTuqX}AK+,G7s.W)n^7(97CMFy\"+XvvrC+;~Q?gmn2=6E#aCB)A,XnTx.`m+GaX2buuSCxA@3Fp['XveB;nxnDwJau`-~.8w8/Agw7s3GGq?q5s\\>9@#\\FQcbn9>bC9D+w#PcF!tEF@r'NBK3gX~DJ.8dD_T\\-fp7X:7aP]B>2hYN\"(\\w8?h2Ye6zkWbNbwS(s-/:J'%XQ`q~D(kZ>.TQ>qw5n`h^QK~Un:E@RxkuP]?mSMYXuu.^:_24F%/a.B=F)y?VUbcPG8t`\\\"jGh;Eb}mK/6P{&7@m:SC;_8\"$Zbx5\\W'/CMKqJ#F5S(z.C`nHHTEGHV]Ur{su2W#<;\\P7aF8?zF?ms^yEGAL-}{r2EwY,%PCVrC;Q)g!p&d2@ZEY@3<P;W)-X*!c5vx{zP2:ur7:X^(`&@y\"ZL4L6)d~Jr8c[+{*X#t#,q9H+T[d@'e&sJ";

    public TokenUtilService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<Authentication> verifyToken(HttpServletRequest request) {
        final String token = request.getHeader(Constants.AUTH_HEADER_NAME);
        if (token != null && !token.isEmpty()) {
            final LoginUser user = parseUserFromToken(request, token.replace(Constants.AUTH_HEADER_TOKEN_PREFIX, "").trim());
            if (user != null) {
                return Optional.of(new UserAuthentication(user));
            }
        }
        return Optional.empty();
    }

    public LoginUser parseUserFromToken(HttpServletRequest request, String token) throws UsernameNotFoundException, IllegalArgumentException {
        Claims claims = getJWTClaims(token.replace(Constants.AUTH_HEADER_TOKEN_PREFIX, ""));
        String userId = claims.getSubject();
        Optional<ApplicationUser> optionalUser = userRepository.findOneByEmail(claims.getSubject());
        if (!optionalUser.isPresent())
            throw new UsernameNotFoundException(String.format("user : %s doesn't exist in wms system!", userId));
        ApplicationUser user = optionalUser.get();
        //user.setRole(UserRole.valueOf(claims.get("role").toString().toUpperCase()));
        return new LoginUser(user);

    }

    private Claims getJWTClaims(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    public String createTokenForUser(ApplicationUser loggedInUser) {
        String hash = RandomStringUtils.randomAlphabetic(64);
        Date date = new Date(System.currentTimeMillis() + Constants.VALIDITY_TIME_MS);
        return createTokenForUser(loggedInUser, hash, date);
    }

    private String createTokenForUser(ApplicationUser user, String hash, Date date) {
        return Jwts.builder()
                //.setExpiration(date)
                .setSubject(user.getEmail()).claim("email", user.getEmail())
                //.claim("role", user.getRole().toString())
                .claim("userKey", hash)
                .signWith(SignatureAlgorithm.HS256, secret).compact();
    }

    public String getEmail(HttpServletRequest request) {
        String token = request.getHeader(Constants.AUTH_HEADER_NAME);
        String jwtToken;
        if (StringUtils.isEmpty(token))
            return null;
        jwtToken = token.replace(Constants.AUTH_HEADER_TOKEN_PREFIX, "").trim();
        Claims claims = getJWTClaims(jwtToken);
        return (String) claims.get("email");
    }


}

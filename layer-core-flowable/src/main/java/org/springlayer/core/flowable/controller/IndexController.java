package org.springlayer.core.flowable.controller;

import org.springlayer.core.flowable.service.MyCurrentUserService;
import org.springlayer.core.flowable.service.PersistentTokenService;
import org.flowable.idm.api.Token;
import org.flowable.ui.common.security.CookieConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.authentication.rememberme.CookieTheftException;
import org.springframework.security.web.authentication.rememberme.InvalidCookieException;
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;

/**
 * @Author Hzhi
 * @Date 2022/3/07 10:19
 * @description
 **/
@Controller
public class IndexController {

    @Resource
    private MyCurrentUserService myCurrentUserService;

    @Resource
    private PersistentTokenService persistentTokenService;

    private static Logger logger = LoggerFactory.getLogger(IndexController.class);

    @RequestMapping(value = "/modeler/index", method = RequestMethod.GET)
    public String modeler(HttpServletRequest request, HttpServletResponse response) {
        myCurrentUserService.initLoginUser(request, response);
        return "index.html";
    }

    @RequestMapping(value = "/modeler/redirect", method = RequestMethod.GET)
    public void redirect(HttpServletRequest request, HttpServletResponse response, String redirectUrl) throws IOException {
        String decodeRedirectUrl = URLDecoder.decode(redirectUrl, "UTF-8");
        logger.debug("跳转URL {}", decodeRedirectUrl);
        response.sendRedirect(decodeRedirectUrl);
    }

    @RequestMapping(value = "/modeler/app/logout", method = RequestMethod.GET)
    public String logout(HttpServletRequest req, HttpServletResponse res) {

        String rememberMeCookie = extractRememberMeCookie(req);
        if (rememberMeCookie != null && rememberMeCookie.length() != 0) {
            try {
                String[] cookieTokens = decodeCookie(rememberMeCookie);
                Token token = getPersistentToken(cookieTokens);
                persistentTokenService.delete(token);
            } catch (InvalidCookieException ice) {
                logger.info("Invalid cookie, no persistent token could be deleted");
            } catch (RememberMeAuthenticationException rmae) {
                logger.debug("No persistent token found, so no token could be deleted");
            }
        }

        Cookie cookie = new Cookie(CookieConstants.COOKIE_NAME, null);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        res.addCookie(cookie);

        return "/modeler/index";
    }

    /**
     * Decodes the cookie and splits it into a set of token strings using the ":"
     * delimiter.
     *
     * @param cookieValue the value obtained from the submitted cookie
     * @return the array of tokens.
     * @throws InvalidCookieException if the cookie was not base64 encoded.
     */
    protected String[] decodeCookie(String cookieValue) throws InvalidCookieException {
        StringBuilder cookieValueBuilder = new StringBuilder(cookieValue);
        int cnt = 4;
        for (int j = 0; j < cookieValueBuilder.length() % cnt; j++) {
            cookieValueBuilder.append("=");
        }
        cookieValue = cookieValueBuilder.toString();

        try {
            Base64.getDecoder().decode(cookieValue.getBytes());
        } catch (IllegalArgumentException e) {
            throw new InvalidCookieException(
                    "Cookie token was not Base64 encoded; value was '" + cookieValue
                            + "'");
        }

        String cookieAsPlainText = new String(Base64.getDecoder().decode(cookieValue.getBytes()));

        String[] tokens = StringUtils.delimitedListToStringArray(cookieAsPlainText,
                ":");

        for (int i = 0; i < tokens.length; i++) {
            try {
                tokens[i] = URLDecoder.decode(tokens[i], StandardCharsets.UTF_8.toString());
            } catch (UnsupportedEncodingException e) {
                logger.error(e.getMessage(), e);
            }
        }

        return tokens;
    }

    /**
     * Locates the Spring Security remember me cookie in the request and returns its
     * value. The cookie is searched for by name and also by matching the context path to
     * the cookie path.
     *
     * @param request the submitted request which is to be authenticated
     * @return the cookie value (if present), null otherwise.
     */
    protected String extractRememberMeCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        if ((cookies == null) || (cookies.length == 0)) {
            return null;
        }

        for (Cookie cookie : cookies) {
            if (CookieConstants.COOKIE_NAME.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }

        return null;
    }

    /**
     * Validate the token and return it.
     */
    protected Token getPersistentToken(String[] cookieTokens) {
        int cnt = 2;
        if (cookieTokens.length != cnt) {
            throw new InvalidCookieException("Cookie token did not contain " + cnt + " tokens, but contained '" + Arrays.asList(cookieTokens) + "'");
        }

        final String presentedSeries = cookieTokens[0];
        final String presentedToken = cookieTokens[1];

        Token token = persistentTokenService.getPersistentToken(presentedSeries);

        if (token == null) {
            // No series match, so we can't authenticate using this cookie
            throw new RememberMeAuthenticationException("No persistent token found for series id: " + presentedSeries);
        }

        // We have a match for this user/series combination
        if (!presentedToken.equals(token.getTokenValue())) {

            // This could be caused by the opportunity window where the token just has been refreshed, but
            // has not been put into the token cache yet. Invalidate the token and refetch and it the new token value from the db is now returned.
            // Note the 'true' here, which invalidates the cache before fetching
            token = persistentTokenService.getPersistentToken(presentedSeries, true);
            if (token != null && !presentedToken.equals(token.getTokenValue())) {

                // Token doesn't match series value. Delete this session and throw an exception.
                persistentTokenService.delete(token);

                throw new CookieTheftException("Invalid remember-me token (Series/token) mismatch. Implies previous cookie theft attack.");

            }
        }
        long maxAge = 2678400 * 1000L;
        if (token != null && System.currentTimeMillis() - token.getTokenDate().getTime() > maxAge) {
            throw new RememberMeAuthenticationException("Remember-me login has expired");
        }
        return token;
    }
}

package fabianopinto.test.testrequest;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Objects;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;

import lombok.extern.java.Log;

@Log
public class DumpRequest {

    public static String dump(HttpServletRequest request) {
        return new DumpRequest(request).dump();
    }

    private HttpServletRequest request;
    private StringBuilder stringBuilder;

    private DumpRequest(HttpServletRequest request) {
        this.request = request;
    }

    private String dump() {
        stringBuilder = new StringBuilder();

        stringBuilder.append("\n==== ").append(LocalDateTime.now()).append(" ====\n");
        append(request.getAuthType(), "authType");
        appendCookies(request.getCookies());
        appendHeaders(request.getHeaderNames());
        append(request.getMethod(), "method");
        append(request.getPathInfo(), "pathInfo");
        append(request.getPathTranslated(), "pathTranslated");
        append(request.getContextPath(), "contextPath");
        append(request.getQueryString(), "queryString");
        append(request.getRemoteUser(), "remoteUser");
        appendUserPrincipal(request.getUserPrincipal());
        append(request.getRequestedSessionId(), "requestedSessionId");
        append(request.getRequestURI(), "requestURI");
        append(request.getServletPath(), "servletPath");
        appendParts();
        stringBuilder.append("=================================\n");

        return stringBuilder.toString();
    }

	private void append(Object value, Object... names) {
        if (value != null && !(value instanceof String && StringUtils.isBlank((String) value))) {
            stringBuilder.append(StringUtils.join(names, "."));
            stringBuilder.append(": ").append(wrap(value)).append("\n");
        }
    }

    private String wrap(Object object) {
        if (object == null) {
            return "null";
        }
        if (object instanceof String && StringUtils.isNotEmpty(object.toString())) {
            return StringUtils.wrap(
                    StringEscapeUtils.escapeJava(object.toString()), "\"");
        }
        return Objects.toString(object).concat(" (")
                .concat(object.getClass().getName()).concat(")");
    }

    private void appendCookies(Cookie[] cookies) {
        if (cookies != null) {
            for (int i = 0; i < cookies.length; i++) {
                append(cookies[i].getValue(), "cookie", cookies[i].getName());
            }
        }
	}

    private void appendHeaders(Enumeration<String> headerNames) {
        if (headerNames != null) {
            while (headerNames.hasMoreElements()) {
                String name = headerNames.nextElement();
                String value = request.getHeader(name);
                append(value, "header", name);
            }
        }
	}

    private void appendUserPrincipal(Principal userPrincipal) {
        if (userPrincipal != null) {
            append(userPrincipal.getName(), "userPrincipal");
        }
	}

    private void appendParts() {
        try {
            Collection<Part> parts = request.getParts();
            if (parts != null) {
                int i = 0;
                for (Part part : parts) {
                    appendPart(part, i++);
                }
            }
        } catch (IOException | ServletException e) {
            log.warning(e.getMessage());
		}
	}

    private void appendPart(Part part, int index) {
        if (part != null) {
            append(part.getName(), "part", index, "name");
            appendPartHeaders(part.getHeaderNames(), part, index);
            append(part.getContentType(), "part", index, "contentType");
            append(part.getSubmittedFileName(), "part", index, "submittedFileName");
            appendPartInputStream(part, index);
        }
	}

    private void appendPartHeaders(Collection<String> headerNames, Part part, int index) {
        if (headerNames != null) {
            for (String name : headerNames) {
                String value = part.getHeader(name);
                append(value, "part", index, "header", name);
            }
        }
	}

    private void appendPartInputStream(Part part, int index) {
        try {
            InputStream inputStream = part.getInputStream();
            String string = IOUtils.toString(inputStream, Charset.defaultCharset());
            append(string, "part", index, "inputStream");
		} catch (IOException e) {
            log.warning(e.getMessage());
		}
    }

}
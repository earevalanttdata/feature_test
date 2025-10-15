package es.nttdata.assetsproxy.infrastructure.apirest.exception;

import es.nttdata.assetsproxy.domain.exception.AssetNotFoundException;
import es.nttdata.assetsproxy.domain.exception.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;

import static org.springframework.http.ResponseEntity.status;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AssetNotFoundException.class)
    public Object handleAssetNotFound(AssetNotFoundException ex, HttpServletRequest req) {
        ProblemDetail pd = problem(HttpStatus.NOT_FOUND, "Asset not found", ex.getMessage(), req,
                "urn:problem-type:asset-not-found");
        return status(HttpStatus.NOT_FOUND).body(pd);
    }

    @ExceptionHandler(BusinessException.class)
    public Object handleBusiness(BusinessException ex, HttpServletRequest req) {
        ProblemDetail pd = problem(HttpStatus.BAD_REQUEST, "Business error", ex.getMessage(), req,
                "urn:problem-type:business-error");
        return status(HttpStatus.BAD_REQUEST).body(pd);
    }

    private ProblemDetail problem(HttpStatus status, String title, String detail,
                                  HttpServletRequest req, String typeUrn) {
        ProblemDetail pd = ProblemDetail.forStatus(status);
        pd.setType(URI.create(typeUrn));
        pd.setTitle(title);
        pd.setDetail(detail);
        pd.setInstance(URI.create(req.getRequestURI()));
        pd.setProperty("method", req.getMethod());
        return pd;
    }
}

package dcc.etudiant_service.FeignClient;

import dcc.etudiant_service.DTO.Filiere;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(
        name = "Filiere-Service",
        url = "http://localhost:8081/v1/filieres"
)
public interface FiliereClient {

    @GetMapping("/{id}")
    @CircuitBreaker(name = "filiereCB")
    @Retry(name = "filiereRetry", fallbackMethod = "filiere-fallback")
    @TimeLimiter(name = "filiereService", fallbackMethod = "filiere-fallback")
    @Cacheable(value = "filiere-cache", key = "#id")
    Filiere getFiliereById(@PathVariable("id") Integer id);

    default Filiere filiereFallback(Integer id, Exception e) {
        return new Filiere(id, "not_existe", "not_existe");
    }
//    default Long Filiere_Bulkhead(Long id,Exception e){
//        return null;
//    }
    @GetMapping("/")
    @Retry(name = "filiereRetry")
    @RateLimiter(name = "filiereRateLimiter")
    @Bulkhead(name = "filiereBulkhead", fallbackMethod ="Filiere_Bulkhead", type = io.github.resilience4j.bulkhead.annotation.Bulkhead.Type.SEMAPHORE)
    List<Filiere> getAllFilieres();
}

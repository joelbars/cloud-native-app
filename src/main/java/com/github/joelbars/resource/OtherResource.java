package com.github.joelbars.resource;

import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.jboss.logging.Logger;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.annotations.jaxrs.PathParam;
import com.github.joelbars.pojo.Other;
import com.github.joelbars.service.OtherRepositoryService;

@Path("/other")
@Produces(MediaType.APPLICATION_JSON)
public class OtherResource {

  private static final Logger LOGGER = Logger.getLogger(OtherResource.class);

  @Inject
  private OtherRepositoryService repository;

  private AtomicLong counter = new AtomicLong(0);

  @GET
  @Retry(maxRetries = 4)
  public List<Other> coffees() {
    final Long invocationNumber = counter.getAndIncrement();

    maybeFail(String.format("OtherResource#coffees() invocation #%d failed", invocationNumber));

    LOGGER.infof("OtherResource#coffees() invocation #%d returning successfully", invocationNumber);
    return repository.getAllCoffees();
  }

  private void maybeFail(String failureLogMessage) {
    if (new Random().nextBoolean()) {
      LOGGER.error(failureLogMessage);
      throw new RuntimeException("Resource failure.");
    }
  }

  @GET
  @Path("/{id}/recommendations")
  @Timeout(250)
  @Fallback(fallbackMethod = "fallbackRecommendations")
  public List<Other> recommendations(@PathParam int id) {
    long started = System.currentTimeMillis();
    final long invocationNumber = counter.getAndIncrement();

    try {
      randomDelay();
      LOGGER.infof("OtherResource#recommendations() invocation #%d returning successfully", invocationNumber);
      return repository.getRecommendations(id);
    } catch (InterruptedException e) {
      LOGGER.errorf("OtherResource#recommendations() invocation #%d timed out after %d ms", invocationNumber,
          System.currentTimeMillis() - started);
      return null;
    }
  }

  @Path("/{id}/availability")
  @GET
  public Response availability(@PathParam int id) {
    final Long invocationNumber = counter.getAndIncrement();

    Other other = repository.getOtherById(id);
    
    if (other == null) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }

    try {
      Integer availability = repository.getAvailability(other);
      LOGGER.infof("OtherResource#availability() invocation #%d returning successfully", invocationNumber);
      return Response.ok(availability).build();
    } catch (RuntimeException e) {
      String message = e.getClass().getSimpleName() + ": " + e.getMessage();
      LOGGER.errorf("OtherResource#availability() invocation #%d failed: %s", invocationNumber, message);
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(message).type(MediaType.TEXT_PLAIN_TYPE)
          .build();
    }
  }

  private void randomDelay() throws InterruptedException {
    Thread.sleep(new Random().nextInt(500));
  }

  public List<Other> fallbackRecommendations(int id) {
    LOGGER.info("Falling back to RecommendationResource#fallbackRecommendations()");
    return Collections.singletonList(repository.getOtherById(1));
  }

}
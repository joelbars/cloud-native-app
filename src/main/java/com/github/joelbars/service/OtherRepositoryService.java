package com.github.joelbars.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;

import com.github.joelbars.pojo.Other;

import org.eclipse.microprofile.faulttolerance.CircuitBreaker;

@ApplicationScoped
public class OtherRepositoryService {

  private Map<Integer, Other> coffeeList = new HashMap<>();
  private AtomicLong counter = new AtomicLong(0);

  public OtherRepositoryService() {
    coffeeList.put(1, new Other(1, "Fernandez Espresso"));
    coffeeList.put(2, new Other(2, "La Scala Whole Beans"));
    coffeeList.put(3, new Other(3, "Dak Lak Filter"));
  }

  public List<Other> getAllCoffees() {
    return new ArrayList<>(coffeeList.values());
  }

  public Other getOtherById(Integer id) {
    return coffeeList.get(id);
  }

  public List<Other> getRecommendations(Integer id) {
    if (id == null) {
      return Collections.emptyList();
    }
    return coffeeList.values().stream().filter(coffee -> !id.equals(coffee.id)).limit(2).collect(Collectors.toList());
  }

  @CircuitBreaker(requestVolumeThreshold = 4)
  public Integer getAvailability(Other coffee) {
    maybeFail();
    return new Random().nextInt(30);
  }

  private void maybeFail() {
    final Long invocationNumber = counter.getAndIncrement();
    if (invocationNumber % 4 > 1) {
      throw new RuntimeException("Service failed.");
    }
  }
}
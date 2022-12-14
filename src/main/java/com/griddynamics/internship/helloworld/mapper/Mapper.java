package com.griddynamics.internship.helloworld.mapper;

/**
 * Mapper interface
 * @param <S> - Type of the object to be mapped.
 * @param <T> - Type of the mapped object.
 */
public interface Mapper<S,T>{
    T map(S source);
}

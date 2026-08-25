package com.caiofabio.usuario.infrastructure.exception;

public class ResourceNotFoundException extends RuntimeException{

    public ResourceNotFoundException(String mensage){
        super(mensage);
    }

    public ResourceNotFoundException(String mensage, Throwable throwable){
        super(mensage,throwable);
    }
}

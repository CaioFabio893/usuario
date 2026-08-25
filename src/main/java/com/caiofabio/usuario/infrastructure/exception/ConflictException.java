package com.caiofabio.usuario.infrastructure.exception;

public class ConflictException extends  RuntimeException{
    public ConflictException(String mensage){
        super(mensage);
    }

    public ConflictException(String mensage, Throwable throwable){
        super(mensage);
    }
}

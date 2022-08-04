package br.ce.wcaquino.servicos.exceptions;

public class FilmeSemEstoqueException extends Exception {

    public FilmeSemEstoqueException(String message) {
        super(message);
    }
}

package jepp.language;

public record JeppMethodPrototype(JeppMethodSignature signature, JeppType returnType, String[] argNames) {}
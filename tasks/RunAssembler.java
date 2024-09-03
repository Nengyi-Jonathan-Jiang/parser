import jepp.assembler.Assembler;
import jepp.jevm.Program;
import jepp.jevm.VM;
import util.FileUtil;

public class RunAssembler {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Wrong number of arguments");
            System.out.println("Usage: java -jar jasm.jar <filename>");
        }

        String fileName = args[0];
        String assemblyProgram = FileUtil.getTextContents(fileName);

        System.out.println("Assembling...");
        Program program = Assembler.assemble(assemblyProgram);

        System.out.println("Executing...");
        VM vm = new VM(System.in, System.out);
        vm.execute(program);
    }
}

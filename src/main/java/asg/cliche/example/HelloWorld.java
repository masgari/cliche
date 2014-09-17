/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package asg.cliche.example;

import asg.cliche.Command;
import asg.cliche.ShellFactory;

import java.io.IOException;

/**
 * 'Hello World!' example.
 *
 * @author ASG
 */
public class HelloWorld {

    public static void main(String[] params) throws IOException {
        ShellFactory.createConsoleShell("hello", null, new HelloWorld())
                .commandLoop();
    }

    @Command
    public int add(int a, int b) {
        return a + b;
    }

    @Command
    public String echo(String s) {
        return s;
    }
}

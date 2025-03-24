package com.judahharris.kohack2025.screen;

import com.judahharris.kohack2025.model.User;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Scanner;

@Getter
@Setter
@AllArgsConstructor
@Component
public class ScreenContext {

    @Autowired
    private ApplicationContext applicationContext;

    private User user;
    private InputStream in;
    private OutputStream out;
    private Scanner scanner;
    private PrintStream printStream;

    public void setIn(InputStream in) {
        this.in = in;
        this.scanner = new Scanner(getIn());
    }

    public void setOut(OutputStream out) {
        this.out = out;
        this.printStream = new PrintStream(out, true);
    }

    public PrintStream getPrintStream() {
        if (printStream == null && out != null) {
            printStream = new PrintStream(out, true);
        }
        return printStream;
    }

    public Scanner getScanner() {
        if (scanner == null && getIn() != null) {
            scanner = new Scanner(getIn());
        }
        return scanner;
    }

    public ScreenContext() {
        setIn(System.in);
        setOut(System.out);
    }

    public ScreenContext(User user, InputStream in, OutputStream out) {
        this.user = user;
        setOut(out);
        setIn(in);
    }

    public ScreenContext(InputStream in, OutputStream out) {
        setOut(out);
        setIn(in);
    }

    public String getUsername() {
        if (user == null) {
            return "Guest";
        }
        return user.getUsername();
    }

    public <T extends Screen> T createScreenBean(Class<T> screenClass, Object... args) {
        Object[] argsIncludingThis = new Object[args.length + 1];
        argsIncludingThis[0] = this;
        System.arraycopy(args, 0, argsIncludingThis, 1, args.length);
        try {
            for (var constructor : screenClass.getConstructors()) {
                T screen;
                if (constructor.getParameterCount() == args.length) {
                    // loop through to check if all the args are the same type
                    boolean allMatch = false;
                    for (int i = 0; i < args.length; i++) {
                        Class<?> paramType = constructor.getParameterTypes()[i];
                        Class<?> argType = args[i].getClass();
                        if ((paramType.isPrimitive() && getWrapperType(paramType).isAssignableFrom(argType)) ||
                                (!paramType.isPrimitive() && paramType.isAssignableFrom(argType))) {                            allMatch = true;
                        } else {
                            allMatch = false;
                            break;
                        }
                    }
                    if (!allMatch) {
//                        System.out.println("allMatch: " + allMatch);
//                        System.out.println("args: " + Arrays.toString(Arrays.stream(args).map(Object::getClass).map(Class::getSimpleName).toArray()));
//                        System.out.println("param types: " + Arrays.toString(Arrays.stream(constructor.getParameterTypes()).map(Class::getSimpleName).toArray()));
                        continue;
                    }
                    screen = (T) constructor.newInstance(args); // Create the instance
                } else if (constructor.getParameterCount() == argsIncludingThis.length) {
                    // loop through to check if all the args are the same type
                    boolean allMatch = false;
                    for (int i = 0; i < argsIncludingThis.length; i++) {
                        if (constructor.getParameterTypes()[i].isAssignableFrom(argsIncludingThis[i].getClass())) {
                            allMatch = true;
                        } else {
                            allMatch = false;
                            break;
                        }
                    }
                    if (!allMatch) {
                        continue;
                    }
                    screen = (T) constructor.newInstance(argsIncludingThis); // Create the instance
                } else {
                    continue;
                }
                applicationContext.getAutowireCapableBeanFactory().autowireBean(screen);
                if (screen.getScreenContext() == null) {
                    screen.setScreenContext(this);
                }
                return screen;
            }

        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        T screen = applicationContext.getAutowireCapableBeanFactory().createBean(screenClass);
        if (screen.getScreenContext() == null) screen.setScreenContext(this);
        return screen;
    }

    private static Class<?> getWrapperType(Class<?> primitiveType) {
        if (primitiveType == boolean.class) return Boolean.class;
        if (primitiveType == byte.class) return Byte.class;
        if (primitiveType == char.class) return Character.class;
        if (primitiveType == short.class) return Short.class;
        if (primitiveType == int.class) return Integer.class;
        if (primitiveType == long.class) return Long.class;
        if (primitiveType == float.class) return Float.class;
        if (primitiveType == double.class) return Double.class;
        return primitiveType; // If it's not a primitive, return as-is
    }

}
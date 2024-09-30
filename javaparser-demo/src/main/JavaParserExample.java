package main;

import java.io.File;
import java.util.Optional;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class JavaParserExample {

    public static void main(String[] args) throws Exception {
        // 解析対象のJavaファイルのパスを指定してください
        File file = new File("C:/Users/Ｋott0/git/javaparser-demo/javaparser-demo/src\\main/Sample.java");
        // JavaParserのインスタンスを作成
        JavaParser parser = new JavaParser();

        // ParseResultを取得
        var result = parser.parse(file);

        // ParseResult から CompilationUnit を取得
        Optional<CompilationUnit> cuOptional = result.getResult();

        // CompilationUnit が存在する場合に解析処理を行う
        if (cuOptional.isPresent()) {
            CompilationUnit cu = cuOptional.get();
            // ASTを走査して context.call(...) を探す
            cu.accept(new MethodCallVisitor(), null);
        } else {
            System.out.println("CompilationUnit の解析に失敗しました");
        }
    }

    private static class MethodCallVisitor extends VoidVisitorAdapter<Void> {
        @Override
        public void visit(MethodCallExpr methodCall, Void arg) {
            super.visit(methodCall, arg);

            // メソッド呼び出しが context.call(...) であるか確認
            if (isContextCall(methodCall)) {
                // クラス名と変数を抽出して表示
                Optional<String> className = getClassNameFromCall(methodCall);
                Optional<String> variable = getVariableFromCall(methodCall);

                className.ifPresent(name -> System.out.println("ClassName: " + name));
                variable.ifPresent(var -> System.out.println("変数: " + var));
            }
        }

        // メソッドが context.call かどうかを確認
        private boolean isContextCall(MethodCallExpr methodCall) {
            return methodCall.getScope().isPresent()
                    && methodCall.getScope().get().toString().equals("context")
                    && methodCall.getNameAsString().equals("call");
        }

        // context.call の最初の引数からクラス名を取得
        private Optional<String> getClassNameFromCall(MethodCallExpr methodCall) {
            if (!methodCall.getArguments().isEmpty()) {
                Expression firstArg = methodCall.getArgument(0);
                if (firstArg instanceof StringLiteralExpr) {
                    StringLiteralExpr classNameExpr = (StringLiteralExpr) firstArg;
                    return Optional.of(classNameExpr.getValue());
                }
            }
            return Optional.empty();
        }

        // context.call の2番目の引数から変数を取得
        private Optional<String> getVariableFromCall(MethodCallExpr methodCall) {
            if (methodCall.getArguments().size() > 1) {
                Expression secondArg = methodCall.getArgument(1);
                if (secondArg instanceof StringLiteralExpr) {
                    StringLiteralExpr variableExpr = (StringLiteralExpr) secondArg;
                    return Optional.of(variableExpr.getValue());
                }
            }
            return Optional.empty();
        }
    }
}

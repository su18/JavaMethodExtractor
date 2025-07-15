package org.su18.extractor;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.Position;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

import java.io.File;
import java.util.List;
import java.util.Optional;

/**
 * @author su18
 */
public class Main {

	public static List<MethodDeclaration> methodList = null;

	public static void main(String[] args) throws Exception {
		if (args.length < 2) {
			System.out.println("Usage: java -jar extractor.jar <source_directory> <line_number>");
			return;
		}

		String     filePath   = args[0];
		String     lineNumber = args[1];
		int        line       = Integer.parseInt(lineNumber);
		File       file       = new File(filePath);
		JavaParser javaParser = new JavaParser();

		ParseResult<CompilationUnit> parse    = javaParser.parse(file);
		Optional<CompilationUnit>    optional = parse.getResult();
		if (optional.isPresent()) {
			CompilationUnit unit = optional.get();
			for (Node childNode : unit.getChildNodes()) {
				if (childNode instanceof ClassOrInterfaceDeclaration classDeclaration) {
					methodList = classDeclaration.getMethods();
					break;
				}
			}
		}

		if (methodList == null) {
			System.out.println("source parse failed");
			return;
		}

		for (MethodDeclaration methodDeclaration : methodList) {
			methodDeclaration.getRange().ifPresent(range -> {
				if (range.begin.isBeforeOrEqual(new Position(line, 0)) && (range.end.isAfterOrEqual(new Position(line, 0)))) {
					System.out.println(methodDeclaration.toString());
				}
			});
		}
	}

}

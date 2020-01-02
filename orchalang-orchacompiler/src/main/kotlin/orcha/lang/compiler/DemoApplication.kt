package orcha.lang.compiler

import orcha.lang.compiler.OrchaCompiler
import orcha.lang.compiler.syntax.ReceiveInstruction
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import java.io.File

@SpringBootApplication
class DemoApplication{

	@Bean
	fun init(orchaCompiler: OrchaCompiler) = CommandLineRunner {
		orchaCompiler . compile(File("src/main/orcha/source").list()[0])
	}

}

fun main(args: Array<String>) {
	runApplication<DemoApplication>(*args)
}



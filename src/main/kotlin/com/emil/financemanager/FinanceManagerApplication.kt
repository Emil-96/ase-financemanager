package com.emil.financemanager

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@SpringBootApplication
class FinanceManagerApplication

data class Greeting(val message: String)

@RestController
class RestController{
	@GetMapping("/hello")
	fun default(): Greeting{
		// model["title"] = "Default"
		return Greeting("Hello World!")
	}
}

fun main(args: Array<String>) {
	runApplication<FinanceManagerApplication>(*args)
}

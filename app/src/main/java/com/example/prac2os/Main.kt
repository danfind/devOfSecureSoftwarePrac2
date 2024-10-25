import java.security.MessageDigest
import kotlin.system.measureTimeMillis

val letters = ('a'..'z').toList()

fun generatePasswords(): List<String> {
    val passwords = mutableListOf<String>()
    for (a in letters) {
        for (b in letters) {
            for (c in letters) {
                for (d in letters) {
                    for (e in letters) {
                        passwords.add("$a$b$c$d$e")
                    }
                }
            }
        }
    }
    return passwords
}

fun hashMD5(text: String): String {
    val digest = MessageDigest.getInstance("MD5")
    return digest.digest(text.toByteArray()).joinToString("") { String.format("%02x", it) }
}

fun hashSHA256(text: String): String {
    val digest = MessageDigest.getInstance("SHA-256")
    return digest.digest(text.toByteArray()).joinToString("") { String.format("%02x", it) }
}

fun bruteForceForHash(passwords: List<String>, hash: String, found: (String) -> Unit) {
    for (password in passwords) {
        if (hashMD5(password) == hash || hashSHA256(password) == hash) {
            found(password)
            return
        }
    }
}

fun bruteForce(passwords: List<String>, hashes: List<String>, multiThreaded: Boolean, numThreads: Int = 1) {
    val elapsed = measureTimeMillis {
        if (multiThreaded) {
            val threads = mutableListOf<Thread>()
            for (hash in hashes) {
                val thread = Thread {
                    bruteForceForHash(passwords, hash) { password ->
                        println("Found password for hash $hash: $password")
                    }
                }
                threads.add(thread)
                thread.start()
            }
            threads.forEach { it.join() }
        } else {
            for (hash in hashes) {
                bruteForceForHash(passwords, hash) { password ->
                    println("Found password for hash $hash: $password")
                }
            }
        }
    }
    println("Execution time: ${elapsed}ms")
}

fun main() {
    val passwords = generatePasswords()
    val predefinedHashes = listOf(
        "1115dd800feaacefdf481f1f9070374a2a81e27880f187396db67958b207cbad",
        "3a7bd3e2360a3d29eea436fcfb7e44c735d117c42d1c1835420b6b9942dd4f1b",
        "74e1bb62f8dabb8125a58852b63bdf6eaef667cb56ac7f7cdba6d7305c50a22f",
        "7a68f09bd992671bb3b19a5e70b7827e"
    )

    var mode: Int
    println("Choose the operation mode:")
    println("1. Single-threaded")
    println("2. Multi-threaded")
    mode = readLine()?.toIntOrNull() ?: 1

    when (mode) {
        1 -> {
            println("Starting single-threaded mode:")
            bruteForce(passwords, predefinedHashes, false)
        }
        2 -> {
            println("Enter the number of threads:")
            val numThreads = readLine()?.toIntOrNull() ?: 1
            println("Starting multi-threaded mode:")
            bruteForce(passwords, predefinedHashes, true, numThreads)
        }
        else -> println("Invalid choice.")
    }
}

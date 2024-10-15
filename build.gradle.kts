plugins {
	java
	jacoco
	id("org.springframework.boot") version "3.2.2"
	id("io.spring.dependency-management") version "1.1.4"
}

group = "rt.marson"
version = "0.0.1-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_21
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
	/**
	 * Spring boot starters
	 */
	implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-websocket")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-freemarker")
	annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
	implementation("org.springframework.boot:spring-boot-starter-mail")


	/**
	 * Database
	 */
	implementation("org.liquibase:liquibase-core")
	runtimeOnly("org.postgresql:postgresql")
	implementation("com.dropbox.core:dropbox-core-sdk:7.0.0")
	implementation("software.amazon.awssdk:s3:2.28.7")
	implementation("software.amazon.awssdk:s3-transfer-manager:2.28.7")
	implementation("org.hibernate.orm:hibernate-spatial:6.6.1.Final")

	/**
	 * Utils & Logging
	 */
	implementation("com.fasterxml.jackson.core:jackson-databind:2.14.2")
	implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
	implementation("org.projectlombok:lombok:1.18.30")
	annotationProcessor("org.projectlombok:lombok:1.18.30")
	implementation("org.mapstruct:mapstruct:1.5.3.Final")
	annotationProcessor("org.mapstruct:mapstruct-processor:1.5.3.Final")
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.0.2")
	implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-csv:2.13.0")
	implementation("org.apache.httpcomponents.client5:httpclient5:5.3.1")
	implementation("commons-io:commons-io:2.16.1")
	implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.17.2")
	//google-maps-places
	implementation("com.google.maps:google-maps-services:2.2.0")

	/**
	 * Security
	 */
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("io.jsonwebtoken:jjwt-api:0.12.6")
	implementation("io.jsonwebtoken:jjwt-impl:0.12.6")
	implementation("io.jsonwebtoken:jjwt-jackson:0.12.6")

	/**
	 * Tests
	 */
	testImplementation("org.junit.jupiter:junit-jupiter-params:5.9.2")
	testImplementation("org.assertj:assertj-core:3.24.2")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.withType<JavaCompile> {
	options.encoding = "UTF-8"
}

val test by tasks.getting(Test::class) { testLogging.showStandardStreams = true }

tasks.test {
	finalizedBy(tasks.jacocoTestReport) // report is always generated after tests run
}

tasks.jacocoTestReport {
	dependsOn(tasks.test) // tests are required to run before generating the report
	reports {
		html.required.set(true)
	}

	classDirectories.setFrom(files(classDirectories.files.map {
		fileTree(it).apply {
			exclude(
					"rt/marson/syeta/domain/**",
					"rt/marson/syeta/config/**",
					"rt/marson/syeta/dto/**",
					"rt/marson/syeta/repository/**",
					"rt/marson/syeta/security/**",
					"rt/marson/syeta/ProjectServiceApplication.class")
		}
	}))
}

tasks.jacocoTestCoverageVerification {
	violationRules {
		rule {
			element = "CLASS"
			excludes = listOf(
					"rt/marson/syeta/domain/**",
					"rt/marson/syeta/config/**",
					"rt/marson/syeta/dto/**",
					"rt/marson/syeta/repository/**",
					"rt/marson/syeta/security/**",
					"rt/marson/syeta/ProjectServiceApplication.class")
			limit {
				minimum = "0.3".toBigDecimal()
			}
		}
	}
}


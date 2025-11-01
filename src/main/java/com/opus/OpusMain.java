package com.opus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.opus.model.TestEntity;
import com.opus.model.User;
import com.opus.repository.TestRepository;
import com.opus.repository.UserRepository;

@SpringBootApplication
public class OpusMain implements CommandLineRunner
{
	@Autowired
	private UserRepository userRepository;
	public static void main(String[] args)
	{
		SpringApplication.run(OpusMain.class, args);
	}

	@Override
	public void run(String... args)
	{

	}
}
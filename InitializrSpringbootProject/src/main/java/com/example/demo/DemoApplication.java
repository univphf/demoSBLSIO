package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootApplication
public class DemoApplication {
    
  
    @Autowired
    JdbcTemplate jdbctmp=new JdbcTemplate();

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
                
	}
 

 @Bean
    public CommandLineRunner run() throws Exception {
        return (String[] args) -> {
             try{
                //create DAtabase
                String sql="CREATE TABLE public.etudiants(id SERIAL PRIMARY KEY,nom text,prenom text,constante text,date_insc text)";
                jdbctmp.execute(sql);
                } catch(DataAccessException e){System.out.println("Table etudiants existe dèja");}
                try{
                String sql="CREATE TABLE public.tokens(token text,nom text,prenom text,date_expi text)";
                jdbctmp.execute(sql);
                } catch(DataAccessException e){System.out.println("Table tokens existe dèja");}
        };
    }
    
 }

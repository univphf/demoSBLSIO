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
    
    
    //AutoWired permet de lier ou "cabler" notre objet jdbctemplate avec les variable de ressources 
    //insérés dans le fichier application.properties
    @Autowired
    JdbcTemplate jdbctmp=new JdbcTemplate();

    
    /*****************************************
     * Point d'entré principal de nos Services
     * @param args 
     ******************************************/
	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);           
	}
 

        /******************************************
         * Methode override run 
         * On demontre ici la possibilité de creer 
         * des tables aux démarrage du service
         * @return
         * @throws Exception 
         ******************************************/
 @Bean
    public CommandLineRunner run() throws Exception {
        return (String[] args) -> {
             try{
                //create table etudiants
                String sql="CREATE TABLE public.etudiants(id SERIAL PRIMARY KEY,nom text,prenom text,constante text,date_insc text)";
                jdbctmp.execute(sql);
                } catch(DataAccessException e){System.out.println("Table etudiants existe dèja");}
                try{
                    //create table tokens
                String sql="CREATE TABLE public.tokens(token text,nom text,prenom text,date_expi text)";
                jdbctmp.execute(sql);
                } catch(DataAccessException e){System.out.println("Table tokens existe dèja");}
        };
    }
    
 }

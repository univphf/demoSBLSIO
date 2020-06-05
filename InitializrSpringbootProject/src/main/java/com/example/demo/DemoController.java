package com.example.demo;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


//annotation qu i indique que cette classe est une classe controller
@RestController
@Api(value = "API REST LSIO DEMO.")

public class DemoController {
   
    //AutoWired permet de lier ou "cabler" notre objet jdbctemplate avec les variable de ressources 
    //insérés dans le fichier application.properties
   @Autowired
   JdbcTemplate jdbctemplate=new JdbcTemplate(); 
   //la classe JdbcTemplate, permet de requêter simplement des BDD en utilisant un Spool de connexions 
   
   //permet de clabler une valeur défini dans le fichier application properties avec une variable de notre programme
   @Value("${jwt.expirationtime}") 
   long EXPIRATIONTIME;
   @Value("${jwt.secret}") 
   String SECRET;

   //Les urls ci dessous permettent d'afficher la documentation automatique Swagger2 de notre Projet.
   //https://localhost:8443/v2/api-docs
   //https://localhost:8443/swagger-ui.html
   
   /*************************************************************
    * EndPoint Hello 
    * Demontre l'utilisation du verbe GET
    * et une réponse au format Json dans le corps de la réponse
    * et une réponse de statut hppt via la classe ResponseEntity
    * @param nom
    * @return 
    *************************************************************/
   @ApiOperation(value = "LSIO : DIRE BONJOUR VIA API ECHO")
   @GetMapping (value="/hello", produces ="application/json" )
    private ResponseEntity<Hello> hello1(@RequestParam String nom)
    {
        return new ResponseEntity<>(new Hello(nom),HttpStatus.OK);
    }
    
    
   /*************************************************************
    * EndPoint HelloSec 
    * Demontre l'utilisation du verbe GET
    * et une réponse au format Json dans le corps de la réponse
    * et une réponse de statut hppt via la classe ResponseEntity
    * Une recupération et un contrôle d'une clé API dans le Header
    * @param nom
    * @param apikey
    * @return 
    *************************************************************/
   @ApiOperation(value = "LSIO : DIRE BONJOUR VERSION SECURISE")
   @GetMapping (value="/helloSec", produces ="application/json" )
    private ResponseEntity<Hello> hello2(@RequestParam String nom, @RequestHeader("x-api-key") String apikey)
    {
        if (controle_api_key(apikey)==false){return new ResponseEntity<>(null,HttpStatus.UNAUTHORIZED);}
        
        return new ResponseEntity<>(new Hello(nom+ " "+apikey),HttpStatus.OK);
    }
    
    
    /**************************************************
     * Lister les étudiants de la table etudiants
     * Demontre l'utilisation et le retour d'une liste
     * sérialisé dans une réponse Json
     * @return 
     **************************************************/
    @ApiOperation(value = "LSIO : LISTER l'ENSEMBLE DES ETUDIANTS DE LA TABLES ETUDIANTS")
    @GetMapping (value="/ListEtudiants", produces="application/json")
    private ResponseEntity<List<Etudiants>> getListEtudiants()
    {
     
    List<Etudiants> etds=new ArrayList<>();
        
        String sql="select id,nom,prenom,constante, date_insc from public.etudiants";
        
       List<Map<String, Object>> rows = jdbctemplate.queryForList(sql);
       
       rows.stream().map((row) -> {
           Etudiants etd = new Etudiants();
           etd.setID((int)row.get("id"));
           etd.setNom(row.get("nom").toString());
           etd.setPrenom(row.get("prenom").toString());
           etd.setConstante(row.get("constante").toString());
           etd.setDate_insc(row.get("date_insc").toString());
           return etd;
       }).forEachOrdered((etd) -> {
           etds.add(etd);
       });
 
       return  new ResponseEntity<>(etds,HttpStatus.OK); 
    }
 

    /***********************************************
     * Lister un étudiant par son numéro d'ID
     * Demontre le retour d'un objet unique dans une
     * réponse Json et la gestion des statut http
     * @param numero
     * @return 
     ***********************************************/
    @ApiOperation(value = "LSIO : AFFICHER UN ETUDIANT DE LA TABLES ETUDIANTS VIA le numero ID = numero de ressource")
    @GetMapping (value="/etudiant", produces="application/json")
    private ResponseEntity<Etudiants> getEtudiant(@RequestParam("numero") int numero)
    {
        
       String sql="select id,nom,prenom,constante,date_insc from public.etudiants where id=?";
      
        Etudiants etd = new Etudiants();
        
       try{
       Map<String, Object> map= jdbctemplate.queryForMap(sql,numero);
       
       if (!map.isEmpty())
       {
            etd.setID((int)map.get("id"));
            etd.setNom(map.get("nom").toString());
            etd.setPrenom(map.get("prenom").toString());
            etd.setConstante(map.get("constante").toString());
            etd.setDate_insc(map.get("date_insc").toString());
       }
       } catch(DataAccessException e) 
       {
       return new ResponseEntity<>(null,HttpStatus.NOT_FOUND);
       }
       
          return  new ResponseEntity<>(etd,HttpStatus.OK);
    }

    
    
    /*************************************************
     * Inserer un nouvel etudiant
     * Utilisation du verbe PUT pour une création 
     * dans la table etudiants
     * Demontre également l'utilisation du contenu du corps
     * de la requête
     * @param etd
     * @return 
     *************************************************/
    @ApiOperation(value = "LSIO : INSERER UN ETUDIANT DE LA TABLES ETUDIANTS")
    @PutMapping(value="/putEtudiant",consumes="application/json")
    private ResponseEntity putEtudiant(@RequestBody Etudiants etd)
    {
        String sql="insert into public.etudiants(nom,prenom,constante,date_insc) values(?,?,?,?)";
        if (jdbctemplate.update(sql,etd.getNom(),etd.getPrenom(),etd.getConstante(),etd.getDate_insc())==-1)
        {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(HttpStatus.CREATED);
    }
    
    
    /********************************************************
     * Supprimer un etudiant par passe du numero en paramètre
     * Demontre l'utilisation du verbe http DELETE
     * @param numero
     * @return 
     ********************************************************/
    @ApiOperation(value = "LSIO : SUPPRIMER UN ETUDIANT DE LA TABLES ETUDIANTS VIA le numero ID = numero de ressource")
    @DeleteMapping(value="/delEtudiant")
    private ResponseEntity deleteEtudiant(@RequestParam("numero") int numero)
    {
        String sql="delete from public.etudiants where id=?";
        if (jdbctemplate.update(sql,numero)==0)
        {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(HttpStatus.OK);
    }
    
    
    
    /*************************************************
     * Supprimer un etudiant
     * Demontre le passage du numero id dans le chemin
     * de l'URL et non plus comme une variable.
     * @param numero
     * @return 
     *************************************************/
    @ApiOperation(value = "LSIO : SUPPRIMER UN ETUDIANT DE LA TABLES ETUDIANTS VIA le numero ID = numero de ressource dans le chemin de l'URL")
    @DeleteMapping(value="/delEtudiant/{numero}")
    private ResponseEntity delete2Etudiant(@PathVariable("numero") int numero)
    {
        String sql="delete from public.etudiants where id=?";
          if (jdbctemplate.update(sql,numero)==0)
        {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(HttpStatus.OK);
    }
    
    
    
    /*************************************************
     * Maj Etudiant
     * Demontre l'utilisation du verbe PATCH
     * @param etd
     * @return
     *************************************************/
    @ApiOperation(value = "LSIO : MAJ D'UN ETUDIANT DE LA TABLES ETUDIANTS")
    @PatchMapping(value="/updateEtudiant", consumes="application/json")
    private ResponseEntity updateEtudiant(@RequestBody Etudiants etd)
    {
        String sql="update public.etudiants set nom=?, prenom=?, constante=?, date_insc=? where id=?";
        
        System.out.println(etd.toString());
        
        try{
        jdbctemplate.update(sql, etd.getNom(),etd.getPrenom(),etd.getConstante(),etd.getDate_insc(),etd.getID());
        } catch (DataAccessException e )
        {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
         return new ResponseEntity(HttpStatus.CREATED);
    }

    
    /**********************************************
     * controler le Token recu dans x-api-key
     * on contrôle son existance dans la base
     * et la date de validité
     * @param apikey
     * @return 
     **********************************************/
    private boolean controle_api_key(String apikey) {
           boolean reponse=false;
           Map<String,Object> map;
        try 
        {
            //requeter la base à la recherche du jeton
          map=jdbctemplate.queryForMap("select token,nom,prenom,date_expi from public.tokens where token=?", apikey);
         }catch(DataAccessException e){return false;}
         
        //recuperer les informations sur ce jeton
        try{
           String token=map.get("token").toString();
           String nom=map.get("nom").toString();
           String prenom=map.get("nom").toString();
           String date_expi=map.get("date_expi").toString();
           Date dateExpi=new SimpleDateFormat("yyyy-MM-dd").parse(date_expi);
        
           //si le jeton est valide et sa date d'expiration n'est pas dépassé retourner vrai
           if (apikey.compareTo(token)==0 && dateExpi.after(new Date())) {return true;}
            
       } catch (ParseException ex) {}       
         return reponse;
    }
    
    
    /**********************************************
     * Demontre la création d'un jeton de type JWT
     * Obtenir un jeton de 24h
     * On insere dans le jeton le nom de l'utilisateur
     * et la date d'expiration
     * @param utilisateur
     * @return 
     **********************************************/
    @ApiOperation(value = "LSIO : RECUPERER UN TOKEN JWT AVEC NOM UTILISATEUR ET DATE EXPIRATION")
     @GetMapping("/gettoken/{utilisateur}")
      private Token getToken(@PathVariable("utilisateur") String utilisateur)
      {
         Token jeton=new Token();  //crer un objet Tokeen
         
         //on encapsule le nom de l'utilisateur
         String JWT = Jwts.builder()
        .setSubject(utilisateur)
        .setExpiration(new Date(System.currentTimeMillis() + EXPIRATIONTIME))  //date expiration = now + x ms
        .signWith(SignatureAlgorithm.HS512, SECRET)  //algorithme d'encodage avec la phrase secrète
        .compact();
         
        jeton.setJeton(JWT); // retour du jeton dans un objet token au format Json
          
        return jeton;
      }

    
     /***********************************
      * Verifier validité du jeton JWT
      * @param jeton
      * @return 
      ***********************************/
    public boolean getValiditeDuJeton(String jeton) {
        Date expiration=null;
    if (jeton != null) {
      // parser le jeton et verifier la date d'expiration.
      expiration = Jwts.parser()
          .setSigningKey(SECRET)
          .parseClaimsJws(jeton)
          .getBody()
          .getExpiration();
    }
    //si le jeton est encore valide
    return expiration.getTime()>System.currentTimeMillis();
  }

    
    
    /************************************************
     * Tester le jeton passé dans le header Authorization
     * @param jeton
     * @return 
     ************************************************/
     @PostMapping("/testToken")
    private ResponseEntity identificationJWT(@RequestHeader(value="Authorization") String jeton)
    {
        String BEARER="Bearer";   //definir le mot Bearer

    if(jeton.startsWith(BEARER)) {jeton = jeton.substring(BEARER.length()+1);} //ne conserver que la partie jeton 

	if (getValiditeDuJeton(jeton)==false)  // tester la validité du jeton et faire une réponse en fonction de...
        {
            return new ResponseEntity("Votre jeton est incorrect",HttpStatus.UNAUTHORIZED);
        }
      return new ResponseEntity("Votre jeton est encore valide",HttpStatus.OK);
    }
   
}

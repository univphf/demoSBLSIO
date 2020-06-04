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

@RestController
@Api(value = "API REST LSIO DEMO.")

public class DemoController {
   
   @Autowired
   JdbcTemplate jdbctemplate=new JdbcTemplate(); 
    
   @Value("${jwt.expirationtime}") 
   long EXPIRATIONTIME;
   @Value("${jwt.secret}") 
   String SECRET;

   
   //https://localhost:8443/v2/api-docs
   //https://localhost:8443/swagger-ui.html
   
   /*****************************************
    * Dire Hello 
    * @param nom
    * @return 
    *****************************************/
   @ApiOperation(value = "LSIO : DIRE BONJOUR")
   @GetMapping (value="/hello", produces ="application/json" )
    public ResponseEntity<Hello> hello1(@RequestParam String nom)
    {
        return new ResponseEntity<>(new Hello(nom),HttpStatus.OK);
    }
    
    
   /*****************************************
    * Dire Hello 
    * @param nom
    * @param apikey
    * @return 
    *****************************************/
   @ApiOperation(value = "LSIO : DIRE BONJOUR VERSION ")
   @GetMapping (value="/helloSec", produces ="application/json" )
    public ResponseEntity<Hello> hello2(@RequestParam String nom, @RequestHeader("x-api-key") String apikey)
    {
        if (controle_api_key(apikey)==false){return new ResponseEntity<>(null,HttpStatus.UNAUTHORIZED);}
        
        return new ResponseEntity<>(new Hello(nom+ " "+apikey),HttpStatus.OK);
    }
    
    
    /****************************************
     * Lister les étudiants
     * @return 
     ****************************************/
    @GetMapping (value="/ListEtudiants", produces="application/json")
    public ResponseEntity<List<Etudiants>> getListEtudiants()
    {
    List<Etudiants> etds=new ArrayList<>();
        
        String sql="select id,nom,prenom,constante, date_insc from public.etudiants";
        
       List<Map<String, Object>> rows = jdbctemplate.queryForList(sql);
       
       for (Map row : rows) {
            Etudiants etd = new Etudiants();
            
            etd.setID((int)row.get("id"));
            etd.setNom(row.get("nom").toString());
            etd.setPrenom(row.get("prenom").toString());
            etd.setConstante(row.get("constante").toString());
            etd.setDate_insc(row.get("date_insc").toString());
            
            etds.add(etd);
        }
 
       return  new ResponseEntity<>(etds,HttpStatus.OK); 
    }
 

    /****************************************
     * Lister un étudiant par son numéro
     * @param numero
     * @return 
     ****************************************/
    @GetMapping (value="/etudiant", produces="application/json")
    public ResponseEntity<Etudiants> getEtudiant(@RequestParam("numero") int numero)
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
       } catch(DataAccessException e) {
       //defaut NOT FOUND
       return new ResponseEntity<>(null,HttpStatus.NOT_FOUND);
       }
       
          return  new ResponseEntity<>(etd,HttpStatus.OK);
    }

    
    
    /*************************************************
     * Inserer un nouvel etudiant
     * @param etd
     * @return 
     *************************************************/
    @PutMapping(value="/putEtudiant",consumes="application/json")
    public ResponseEntity putEtudiant(@RequestBody Etudiants etd)
    {
        String sql="insert into public.etudiants(nom,prenom,constante,date_insc) values(?,?,?,?)";
        if (jdbctemplate.update(sql,etd.getNom(),etd.getPrenom(),etd.getConstante(),etd.getDate_insc())==-1)
        {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(HttpStatus.CREATED);
    }
    
    
    /*************************************************
     * Supprimer un etudiant
     * @param numero
     * @return 
     *************************************************/
    @DeleteMapping(value="/delEtudiant")
    public ResponseEntity deleteEtudiant(@RequestParam("numero") int numero)
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
     * @param numero
     * @return 
     *************************************************/
    @DeleteMapping(value="/delEtudiant/{numero}")
    public ResponseEntity delete2Etudiant(@PathVariable("numero") int numero)
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
     * @param etd
     * @return
     *************************************************/
    @PatchMapping(value="/updateEtudiant", consumes="application/json")
    public ResponseEntity updateEtudiant(@RequestBody Etudiants etd)
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
     * controler le Token
     * @param apikey
     * @return 
     **********************************************/
    private boolean controle_api_key(String apikey) {
           boolean reponse=false;
           Map<String,Object> map;
        try {
          map=jdbctemplate.queryForMap("select token,nom,prenom,date_expi from public.tokens where token=?", apikey);
         }catch(DataAccessException e){return false;}
         
        try{
           String token=map.get("token").toString();
           String nom=map.get("nom").toString();
           String prenom=map.get("nom").toString();
           String date_expi=map.get("date_expi").toString();
           Date dateExpi=new SimpleDateFormat("yyyy-MM-dd").parse(date_expi);
           
           if (apikey.compareTo(token)==0 && dateExpi.after(new Date())) {reponse=true;}
            
       } catch (ParseException ex) {}       
         return reponse;
    }
    
    /***********************************
     * Obtenir un jeton de 24h
     * @param utilisateur
     * @return 
     ***********************************/
     @GetMapping("/gettoken/{utilisateur}")
      public Token getToken(@PathVariable("utilisateur") String utilisateur)
      {
         Token jeton=new Token();
         String JWT = Jwts.builder()
        .setSubject(utilisateur)
        .setExpiration(new Date(System.currentTimeMillis() + EXPIRATIONTIME))
        .signWith(SignatureAlgorithm.HS512, SECRET)
        .compact();
         
        jeton.setJeton(JWT);
          
        return jeton;
      }

    
     /***********************************
      * Verifier validité du jeton
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
     * Tester le jeton passé dans authorization
     * @param jeton
     * @return 
     ************************************************/
     @PostMapping("/testToken")
    private ResponseEntity identification2(@RequestHeader(value="Authorization") String jeton)
    {
        String BEARER="Bearer";   

    if(jeton.startsWith(BEARER)) {jeton = jeton.substring(BEARER.length()+1);}

	if (getValiditeDuJeton(jeton)==false)
        {
            return new ResponseEntity("Votre jeton est incorrect",HttpStatus.UNAUTHORIZED);
        }
      return new ResponseEntity("Votre jeton est encore valide",HttpStatus.OK);
    }

    
}

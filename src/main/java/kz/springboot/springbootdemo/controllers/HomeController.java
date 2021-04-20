package kz.springboot.springbootdemo.controllers;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.apache.commons.codec.digest.DigestUtils;
import kz.springboot.springbootdemo.db.DBManager;
import kz.springboot.springbootdemo.db.Shopitem;
import kz.springboot.springbootdemo.db.Tasks;
import kz.springboot.springbootdemo.entities.*;
import kz.springboot.springbootdemo.services.ItemService;
import kz.springboot.springbootdemo.services.UserService;
import org.apache.tomcat.util.http.fileupload.IOUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.bind.DatatypeConverter;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Date;
import java.util.*;
import java.util.List;

import static java.util.Collections.reverse;

@Controller

public class HomeController {


        private  SecretKeySpec secretKey;
        private  byte[] key;

        public  void setKey(String myKey)
        {
            MessageDigest sha = null;
            try {
                key = myKey.getBytes("UTF-8");
                sha = MessageDigest.getInstance("SHA-1");
                key = sha.digest(key);
                key = Arrays.copyOf(key, 16);
                secretKey = new SecretKeySpec(key, "AES");
            }
            catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        public  String encrypt(String strToEncrypt, String secret)
        {
            try
            {
                setKey(secret);
                Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
                cipher.init(Cipher.ENCRYPT_MODE, secretKey);
                return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")));
            }
            catch (Exception e)
            {
                System.out.println("Error while encrypting: " + e.toString());
            }
            return null;
        }

        public  String decrypt(String strToDecrypt, String secret)
        {
            try
            {
                setKey(secret);
                Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
                cipher.init(Cipher.DECRYPT_MODE, secretKey);
                return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
            }
            catch (Exception e)
            {
                System.out.println("Error while decrypting: " + e.toString());
            }
            return null;
        }

    @Autowired
    private ItemService itemService;
    @Autowired
    private UserService userService;


    @Value("${file.avatar.viewPath}")
        private String viewPath;

    @Value("${file.avatar.uploadPath}")
    private String uploadPath;

    @Value("${file.avatar.defaultPicture}")
    private String defaultPicture;

    @GetMapping(value = "/")
    public String index(Model model){
        List<Categories> cats = itemService.getAllCategories();
        model.addAttribute("cats", cats);
        List<Items> items = itemService.getAllItems();
        model.addAttribute("items", items);
        List<Brands> brands = itemService.getAllBrands();
        model.addAttribute("brands", brands);
        model.addAttribute("currentUser", getUserData());
        return "index";
    }

    @GetMapping(value = "/about")
    public String about(){
        return "about";
    }

    @GetMapping(value = "/admin")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_MODERATOR')")
    public String admin(Model model){
        model.addAttribute("currentUser", getUserData());
        ArrayList<Items> items = (ArrayList<Items>) itemService.getAllItems();
        model.addAttribute("items", items);

        List<Countries> countries = itemService.getAllCountries();
        model.addAttribute("countries", countries);

        List<Brands> brands = itemService.getAllBrands();
        model.addAttribute("brands", brands);



        List<Roles> roles = userService.getAllRoles();
        List<Users> users = userService.getAllUsers();
        model.addAttribute("users", users);
        model.addAttribute("roles", roles);
        List<Categories> cats = itemService.getAllCategories();
        model.addAttribute("cats", cats);


        return "admin";
    }


        @PostMapping(value = "/additem")
        @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_MODERATOR')")
    public String addItem(@RequestParam(name="name") String name,
                          @RequestParam(name="desc") String desc,
                          @RequestParam(name="price") Integer price,
                          @RequestParam(name="brand_id", defaultValue = "0") Long id,
                          @RequestParam(name="stars") Integer stars,
                          @RequestParam(name="s_url") String s_url,
                          @RequestParam(name="l_url") String l_url

        )



        {
            Brands br = itemService.getBrand(id);

            if(br!=null){

                itemService.addItem(new Items(null,name,desc,price,stars,s_url,l_url, new Date(System.currentTimeMillis()), false,br, null,null,null,null));
            }


        return "redirect:/admin";
        }

    @PostMapping(value = "/addcat")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public String addCat(@RequestParam(name="name") String name,
                          @RequestParam(name="url") String url


    )



    {


            itemService.addCategory(new Categories(null,name,url));



        return "redirect:/admin";
    }

    @PostMapping(value = "/addbr")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String addBr(@RequestParam(name="name") String name,

                          @RequestParam(name="country_id", defaultValue = "0") Long id


    )



    {
        Countries br = itemService.getCountry(id);

        if(br!=null){

            itemService.addBrand(new Brands(null,name,br,null));
        }


        return "redirect:/admin";
    }
    @PostMapping(value = "/addctn")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public String addCtn(@RequestParam(name="name") String name,
                          @RequestParam(name="code") String code


    )



    {




            itemService.addCountry(new Countries(null,name,code,null));



        return "redirect:/admin";
    }




    @GetMapping(value = "/search2")
    public String search3(Model m,
                          @RequestParam(name="name",defaultValue = "%",required = false) String name,
                          @RequestParam(name="price1",defaultValue = "-1",required = false) double price1,
                          @RequestParam(name="price2",defaultValue = "-1",required = false) double price2,
                          @RequestParam(name="brand",defaultValue = "-2",required = false) Long br_id,
                          @RequestParam(name="cat",defaultValue = "-2",required = false) Long cat_id,
                          @RequestParam(name="order",defaultValue = "asc", required = false) String order

    )

    { List<Brands> brands = itemService.getAllBrands();
        List<Categories> cats = itemService.getAllCategories();
        m.addAttribute("cats", cats);
        m.addAttribute("brands", brands);

        m.addAttribute("currentUser", getUserData());

        List<Items> items= new ArrayList<>();


            if (!name.equals("") && price1 == -1 && price2 == -1 && order.equals("asc"))
                items = itemService.bynameAsc(name);

            if (!name.equals("") && price1 == -1 && price2 == -1 && order.equals("desc"))
                items = itemService.bynameDesc(name);


            if (!name.equals("") && price1 != -1 && price2 != -1 && order.equals("asc"))
                items = itemService.bynamepriceAsc(name, price1, price2);

            if (!name.equals("") && price1 != -1 && price2 != -1 && order.equals("desc"))
                items = itemService.bynamepriceDesc(name, price1, price2);


        if(br_id != -1 && cat_id==-1) {

            List<Items> items2 = new ArrayList<>();
            for (Items i : items
            ) {
                if (i.getBrand().getId().equals(br_id))
                    items2.add(i);

            }
            m.addAttribute("items", items2);
        }

        else

        if(br_id == -1 && cat_id!=-1) {

            List<Items> items2 = new ArrayList<>();
            for (Items i : items
            ) {
                if (i.getCategories().contains(itemService.getCategory(cat_id)))
                    items2.add(i);

            }
            m.addAttribute("items", items2);

        }

        else


        if(br_id != -2 && cat_id!=-2) {
            List<Items> items2 = new ArrayList<>();

            for (Items i : items
            ) {
                if (i.getBrand().getId().equals(br_id) && i.getCategories().contains(itemService.getCategory(cat_id)))
                    items2.add(i);

            }

            m.addAttribute("items", items2);
        }


          else  if(br_id != -2 || cat_id!=-2) {
                List<Items> items2 = new ArrayList<>();

                if (br_id != -2) {


                    for (Items i : items
                    ) {
                        if (i.getBrand().getId().equals(br_id))
                            items2.add(i);

                    }


                }
                if (cat_id != -2) {


                    for (Items i : items
                    ) {
                        if (i.getCategories().contains(itemService.getCategory(cat_id)))
                            items2.add(i);

                    }


                }


                m.addAttribute("items", items2);
            }
else


            m.addAttribute("items", items);


        return "search";
    }




    @GetMapping(value = "/user_details/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public String userdetails(Model m,@PathVariable(name = "id") Long id){
        Users item = userService.getUser(id);
        List<Roles> roles = userService.getAllRoles();
        m.addAttribute("roles", roles);
        m.addAttribute("user", item);
        m.addAttribute("currentUser", getUserData());
        return "edit_user";
    }


    @PostMapping(value = "/saveitem")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_MODERATOR')")
    public String saveTask(
            @RequestParam(name="id") Long id,
            @RequestParam(name="name") String name,
                           @RequestParam(name="desc") String desc,
                           @RequestParam(name="price") double price,

                           @RequestParam(name="stars") Integer stars,
                           @RequestParam(name="s_url") String s_url,
                           @RequestParam(name="l_url") String l_url,
                           @RequestParam(name="del", defaultValue = "0") int del

                           )
    {

        if(del == 1){
            itemService.deleteItem(itemService.getItem(id));
            DBManager.delTask(id);
            return "redirect:/admin";
        }
        else{
            Items i = itemService.getItem(id);
            i.setName(name);
            i.setDescription(desc);
            i.setPrice(price);
            i.setLargePicURL(l_url);
            i.setStars(stars);
            i.setSmallPicURL(s_url);

            itemService.saveItem(i);

       return "redirect:/item_details/ "+ i.getId ();
    }}

    @PostMapping(value = "/assigncategory")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_MODERATOR')")
    public String assCat(
            @RequestParam(name="item_id") Long iid,
            @RequestParam(name="category_id") Long cid,
            @RequestParam(name="del",defaultValue = "0") int del

    )
    {

        if(del == 1){

            Categories cat = itemService.getCategory(cid);
            if (cat != null) {
                Items item = itemService.getItem(iid);
                if (item != null) {
                    List<Categories> categories = item.getCategories();

                    categories.remove(cat);
                    itemService.saveItem(item);
                    return "redirect:/item_details/" + iid +"#categories_tables";
                }

            }

        }
        else {

            Categories cat = itemService.getCategory(cid);
            if (cat != null) {
                Items item = itemService.getItem(iid);
                if (item != null) {
                    List<Categories> categories = item.getCategories();
                    if (categories == null) {
                        categories = new ArrayList<>();
                    }
                    categories.add(cat);
                    itemService.saveItem(item);
                    return "redirect:/item_details/" + iid +"#categories_tables";
                }

            }
        }


            return "redirect:/";
    }

    @PostMapping(value = "/assignrole")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public String assignrole(
            @RequestParam(name="user_id") Long iid,
            @RequestParam(name="role_id") Long cid,
            @RequestParam(name="del",defaultValue = "0") int del

    )
    {

        if(del == 1){

            Roles cat = userService.getRole(cid);
            if (cat != null) {
                Users user = userService.getUser(iid);
                if (user != null) {
                    List<Roles> categories = user.getRoles();

                    categories.remove(cat);
                    userService.saveUser(user);
                    return "redirect:/user_details/" + iid +"#roles_table";
                }

            }

        }
        else {

            Roles cat = userService.getRole(cid);
            if (cat != null) {
                Users user = userService.getUser(iid);
                if (user != null) {
                    List<Roles> categories = user.getRoles();
                    if (categories == null) {
                        categories = new ArrayList<>();
                    }
                    categories.add(cat);
                    userService.saveUser(user);
                    return "redirect:/user_details/" + iid +"#roles_table";
                }

            }
        }


        return "redirect:/";
    }
    @PostMapping(value = "/edctn")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public String saveCtn(
            @RequestParam(name="id") Long id,
            @RequestParam(name="name") String name,
            @RequestParam(name="code") String desc,

            @RequestParam(name="del", defaultValue = "0") int del

    )
    {

        if(del == 1){
            itemService.deleteCtn(itemService.getCountry(id));

            return "redirect:/admin";
        }
        else{
            Countries i = itemService.getCountry(id);
            i.setName(name);
            i.setCode(desc);


            itemService.saveCtn(i);

            return "redirect:/admin";}
    }

    @PostMapping(value = "/edcat")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public String edCat(
            @RequestParam(name="id") Long id,
            @RequestParam(name="name") String name,
            @RequestParam(name="url") String desc,

            @RequestParam(name="del", defaultValue = "0") int del

    )
    {

        if(del == 1){
            itemService.deleteCategory(itemService.getCategory(id));

            return "redirect:/admin";
        }
        else{
            Categories i = itemService.getCategory(id);
            i.setName(name);
            i.setLogoURL(desc);


            itemService.saveCategory(i);

            return "redirect:/admin";}
    }


    @PostMapping(value = "/edbr")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public String saveBr(
            @RequestParam(name="id") Long id,
            @RequestParam(name="name") String name,
            @RequestParam(name="country_id") Long ctn_id,

            @RequestParam(name="del", defaultValue = "0") int del

    )
    {

        if(del == 1){
            itemService.deleteBr(itemService.getBrand(id));

            return "redirect:/admin";
        }
        else{

            Brands i = itemService.getBrand(id);
            i.setName(name);
            i.setCountry(itemService.getCountry(ctn_id));


            itemService.saveBr(i);

            return "redirect:/admin";

        }
    }



    @GetMapping(value = "/403")
    public String accessDenied(Model m){
        m.addAttribute("currentUser", getUserData());
        return "403";
    }

    @GetMapping(value = "/login")
    public String login(Model m){
        m.addAttribute("currentUser", getUserData());
        return "login";
    }

    @GetMapping(value = "/profile")
    @PreAuthorize("isAuthenticated()")
    public String profile(Model m){
        m.addAttribute("currentUser", getUserData());
        return "profile";
    }
    @GetMapping(value = "/register")

    public String reg(Model m){
        m.addAttribute("currentUser", getUserData());
        return "register";
    }

    @PostMapping(value = "/reg")

    public String regis(@RequestParam(name="name") String name,
                         @RequestParam(name="pass") String pass,
                        @RequestParam(name="pass2") String pass2,
                        @RequestParam(name="email") String email
,
                        RedirectAttributes redirAttrs
    )



    {


        if(pass.equals(pass2)){
        ArrayList<Roles> r = new ArrayList<Roles>();
        r.add(userService.getRole(1L));
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        userService.addUser(new Users(null,email,passwordEncoder.encode(pass), name, null,r));


        redirAttrs.addFlashAttribute("success", "Successfully registred");

        return "redirect:/login";}
        else
        {redirAttrs.addFlashAttribute("error", "Registration error");
            return "redirect:/register?error";}
    }

    @PostMapping(value = "/adduser")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public String adduser(@RequestParam(name="name") String name,
                        @RequestParam(name="pass") String pass,

                        @RequestParam(name="email") String email

    )



    {


            ArrayList<Roles> r = new ArrayList<Roles>();
            r.add(userService.getRole(1L));
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

            userService.addUser(new Users(null,email,passwordEncoder.encode(pass), name,null, r));


          return "redirect:/admin";
    }

    @PostMapping(value = "/addrole")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public String addrole(@RequestParam(name="role") String role,
                          @RequestParam(name="desc") String desc


    )



    {




        userService.addRole(new Roles(null,role,desc));


        return "redirect:/admin";
    }

    @PostMapping(value = "/eduser")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public String saveUser(
            @RequestParam(name="id") Long id,
            @RequestParam(name="name") String name,
            @RequestParam(name="pass") String pass,

            @RequestParam(name="email") String email,
            @RequestParam(name="del", defaultValue = "0") int del

    )
    {

        if(del == 1){
            userService.deleteUser(userService.getUser(id));

            return "redirect:/admin" ;
        }
        else{
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            Users i = userService.getUser(id);
            i.setFullName(name);
            i.setEmail(email);


            i.setPassword(passwordEncoder.encode(pass));



            userService.saveUser(i);

            return "redirect:/user_details/" + id ;}
    }
    @PostMapping(value = "/edprofile")

    public String saveUser2(
            @RequestParam(name="id") Long id,
            @RequestParam(name="name") String name,
RedirectAttributes redirAttrs

    )
    {

            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            Users i = userService.getUser(id);
            i.setFullName(name);

            userService.saveUser(i);
            redirAttrs.addFlashAttribute("success1", "Profile successfully updated.");
            return "redirect:/profile" ;
    }
    @PostMapping(value = "/edpass")

    public String saveUser2(
            @RequestParam(name="id") Long id,
            @RequestParam(name="old") String old,

            @RequestParam(name="new") String new1,
            @RequestParam(name="new2") String new2,RedirectAttributes redirAttrs


    )
    {


        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        if (passwordEncoder.matches(old, getUserData().getPassword())){
            Users i = userService.getUser(id);
            if(new1.equals(new2)){
            i.setPassword(passwordEncoder.encode(new1));
            userService.saveUser(i);}
            else {

                redirAttrs.addFlashAttribute("errorP", "Confirm password doesnt match.");
                return "redirect:/profile?differentpasswords" ;}
        }
        else  {


            redirAttrs.addFlashAttribute("errorP2", " Old password doesnt match.");
            return "redirect:/profile?olddoesntmatch" ;}






        redirAttrs.addFlashAttribute("successP", "Password successfully updated.");
        return "redirect:/profile" ;
    }


    @PostMapping(value = "/uploadavatar")
    @PreAuthorize("isAuthenticated()")
    public String avatar(
            @RequestParam(name="user_ava") MultipartFile file
            ,RedirectAttributes redirAttrs

    )
    {
if(file.getContentType().equals("image/jpeg") || file.getContentType().equals("image/png")) {

    try {
        Users currentUser = getUserData();

        String picName = DigestUtils.sha1Hex("avatar_"+currentUser.getId()+"_!Picture");
        byte[] bytes = file.getBytes();
        Path path = Paths.get(uploadPath + picName+".jpg");
        Files.write(path, bytes);
        currentUser.setUserAvatar(picName);
        userService.saveUser(currentUser);
        redirAttrs.addFlashAttribute("successA", "Successfully updated avatar.");
        return "redirect:/profile?success" ;
    } catch (Exception e) {
        e.printStackTrace();
    }
}

        redirAttrs.addFlashAttribute("errorA", "Error download avatar.");
        return "redirect:/profile" ;
    }



    @GetMapping(value = "/viewphoto/{url}", produces={MediaType.IMAGE_JPEG_VALUE})

    public @ResponseBody byte[] viewProfilePhoto(@PathVariable(name = "url") String url) throws IOException {
String pictureURL = viewPath+defaultPicture;
if(url!=null){
    pictureURL=viewPath+url+".jpg";
}
        InputStream in;
try {
    ClassPathResource resource = new ClassPathResource(pictureURL);
    in = resource.getInputStream();
}
catch (Exception e){
    ClassPathResource resource = new ClassPathResource(viewPath+defaultPicture);
    in = resource.getInputStream();
    e.printStackTrace();
}

return org.apache.commons.io.IOUtils.toByteArray(in);
    }





    private Users getUserData(){
        Authentication authontication = SecurityContextHolder.getContext().getAuthentication();
        if(!(authontication instanceof AnonymousAuthenticationToken)){
            User secUser = (User)authontication.getPrincipal();
            Users myUser = userService.getUserByEmail(secUser.getUsername());
            return myUser;
        }
        return null;
    }
    @GetMapping(value = "/details/{id}")
    public String details(Model m,@PathVariable(name = "id") Long id){
        Items item = itemService.getItem(id);
        m.addAttribute("t", item);
        List<Brands> brands = itemService.getAllBrands();
        m.addAttribute("brands", brands);
        m.addAttribute("currentUser", getUserData());
        m.addAttribute("pics", item.getPictures());
        List<Categories> cats = itemService.getAllCategories();
        m.addAttribute("cats", cats);



        return "details";
    }

    @GetMapping(value = "/item_details/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_MODERATOR')")
    public String itemdetails(Model m,@PathVariable(name = "id") Long id){
        Items item = itemService.getItem(id);
        List<Categories> cats = itemService.getAllCategories();
        m.addAttribute("cats", cats);
        m.addAttribute("t", item);
        m.addAttribute("currentUser", getUserData());
        List<Brands> brands =   itemService.getAllBrands();
        m.addAttribute("pics", item.getPictures() );
        m.addAttribute("brands", brands);

        return "details_edit";
    }

    @PostMapping(value = "/addPic")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_MODERATOR')")
    public String addPic(
            @RequestParam(name="pic") MultipartFile file,
            @RequestParam(name="item_id") Long item_id

    )
    {
        if(file.getContentType().equals("image/jpeg") || file.getContentType().equals("image/png")) {

            try {



                String picName = DigestUtils.sha1Hex("pic_"+file.getOriginalFilename()+"_!Picture");
                byte[] bytes = file.getBytes();

                Path path = Paths.get(uploadPath + picName+".jpg");

                Files.write(path, bytes);

                itemService.addPicture(new Pictures(null,picName,new Date(System.currentTimeMillis()),itemService.getItem(item_id) ));

                return "redirect:/item_details/"+ item_id +"?success";


            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        return "redirect:/item_details/"+ item_id;
    }

    @PostMapping(value = "/delPic")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_MODERATOR')")
    public String dPic(

            @RequestParam(name="pic_id") Long item_id,
            @RequestParam(name="item_id") Long item2_id

    )
    {
         itemService.deletePicture(itemService.getPicture(item_id));


        return "redirect:/item_details/"+ item2_id;
    }



    @PostMapping(value = "/toBasket")

    public String toBasket(
            HttpServletRequest request, HttpServletResponse response,
            @RequestParam(name="item_id") Long item_id,
            @RequestParam(name="basket", defaultValue = "0") int bas,
            @RequestParam(name="del", defaultValue = "0") int del,
            RedirectAttributes redirAttrs

    ) throws ParseException {


        Cookie[] cookies = request.getCookies();
        String cookieName = "card";
        Cookie cookie = null;
        if(cookies !=null) {
            for(Cookie c: cookies) {
                if(cookieName.equals(c.getName())) {
                    cookie = c;

                    break;
                }
            }
        }
           if(del == 0) {

               if (cookie == null) {
                   JSONArray ar = new JSONArray();
                   ar.add(item_id);
                   final String secretKey = "ssshhhhhhhhhhh!!!!";

                   response.addCookie(new Cookie("card", encrypt(ar.toJSONString(), secretKey)));
               } else {

                   final String secretKey = "ssshhhhhhhhhhh!!!!";

                   //String originalString = "howtodoinjava.com";
                   // String encryptedString = AES.encrypt(cookie.getValue(), secretKey) ;
                   String decryptedString = decrypt(cookie.getValue(), secretKey);

                   JSONParser jsonParser = new JSONParser();
                   JSONArray jo = (JSONArray) jsonParser.parse(decryptedString);


                   jo.add(item_id);


                   response.addCookie(new Cookie("card", encrypt(jo.toJSONString(), secretKey)));

               }


               if (bas == 0)
               {

                   redirAttrs.addFlashAttribute("success", "Successfully added to basket.");

                   return "redirect:/details/" + item_id;}
               else {


                   redirAttrs.addFlashAttribute("successA", "Successfully added to basket.");
                   return "redirect:/basket";}
           }
           else {



                   final String secretKey = "ssshhhhhhhhhhh!!!!";

                   //String originalString = "howtodoinjava.com";
                   // String encryptedString = AES.encrypt(cookie.getValue(), secretKey) ;
                   String decryptedString = decrypt(cookie.getValue(), secretKey);

                   JSONParser jsonParser = new JSONParser();
                   JSONArray jo = (JSONArray) jsonParser.parse(decryptedString);

               reverse(jo);
               for(Object j: jo
               ) {
                   if ((long) Integer.parseInt(j.toString()) == item_id){
                       jo.remove(j);
                       break;
                   }


               }
               reverse(jo);


                   response.addCookie(new Cookie("card", encrypt(jo.toJSONString(), secretKey)));




               if (bas == 0){

                   redirAttrs.addFlashAttribute("success", "Successfully deleted from basket.");

                   return "redirect:/details/" + item_id;}
               else {


                   redirAttrs.addFlashAttribute("successA", "Successfully deleted from basket.");
                   return "redirect:/basket";}
    }




    }


    @GetMapping(value = "/basket")
    public String basket(Model m,  HttpServletRequest request) throws ParseException {
        Cookie[] cookies = request.getCookies();
        String cookieName = "card";
        Cookie cookie = null;
        if(cookies !=null) {
            for(Cookie c: cookies) {
                if(cookieName.equals(c.getName())) {
                    cookie = c;

                    break;
                }
            }
        }
class ia{
            Items item;
            int amount;

    public Items getItem() {
        return item;
    }

    public int getAmount() {
        return amount;
    }

    public ia(Items item, int amount) {
        this.item = item;
        this.amount = amount;
    }
}
        double total = 0;
        ArrayList<Items> card = new ArrayList<>();
        final String secretKey = "ssshhhhhhhhhhh!!!!";
        if (cookie != null)
        {

            ArrayList<ia> ia= new ArrayList<>();
            String decryptedString = decrypt(cookie.getValue(), secretKey) ;

            JSONParser jsonParser = new JSONParser();

            JSONArray jo = (JSONArray) jsonParser.parse(decryptedString);

            for(Object j: jo
                 ) {
                card.add(itemService.getItem((long) Integer.parseInt(j.toString())));
                total += itemService.getItem((long) Integer.parseInt(j.toString())).getPrice();
                System.out.println(j.toString());

            }

            ArrayList<Long> povtor = new ArrayList<>();
            for (Items i: card
                 ) {


                if(!povtor.contains(i.getId())){
                    ia.add(new ia(i, Collections.frequency(card, i) ));
                    povtor.add(i.getId());
                }


            }




            m.addAttribute("card", ia);

        }


        else
            m.addAttribute("card", null);

        List<Sold> solds =   itemService.getAllSolds();
        List<Brands> brands =   itemService.getAllBrands();
      m.addAttribute("solds", solds);
        m.addAttribute("currentUser", getUserData());
        m.addAttribute("total", total);
        m.addAttribute("brands", brands);
        List<Categories> cats = itemService.getAllCategories();
        m.addAttribute("cats", cats);
        return "basket";
    }



    @PostMapping(value = "/checkin")

    public String check(

            HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirAttrs
    ) throws ParseException {final String secretKey = "ssshhhhhhhhhhh!!!!";
        Cookie[] cookies = request.getCookies();
        String cookieName = "card";
        Cookie cookie = null;
        if(cookies !=null) {
            for(Cookie c: cookies) {
                if(cookieName.equals(c.getName())) {
                    cookie = c;

                    break;
                }
            }
        }
        String decryptedString = decrypt(cookie.getValue(), secretKey) ;

        JSONParser jsonParser = new JSONParser();

        JSONArray jo = (JSONArray) jsonParser.parse(decryptedString);

        for(Object j: jo
        ) {
            itemService.addSold(new Sold(null,itemService.getItem((long) Integer.parseInt(j.toString())),new Date(System.currentTimeMillis())));



        }
        jo.clear();
        response.addCookie(new Cookie("card", encrypt(jo.toJSONString(), secretKey)));


        redirAttrs.addFlashAttribute("success", "Successfull check in.");
        return "redirect:/basket";
    }

    @PostMapping(value = "/delbasket")

    public String check2(

            HttpServletRequest request, HttpServletResponse response,RedirectAttributes redirAttrs
    ) throws ParseException {
        final String secretKey = "ssshhhhhhhhhhh!!!!";
        Cookie[] cookies = request.getCookies();
        String cookieName = "card";
        Cookie cookie = null;
        if(cookies !=null) {
            for(Cookie c: cookies) {
                if(cookieName.equals(c.getName())) {
                    cookie = c;

                    break;
                }
            }
        }
        String decryptedString = decrypt(cookie.getValue(), secretKey) ;

        JSONParser jsonParser = new JSONParser();

        JSONArray jo = (JSONArray) jsonParser.parse(decryptedString);

        jo.clear();
        response.addCookie(new Cookie("card", encrypt(jo.toJSONString(), secretKey)));

        redirAttrs.addFlashAttribute("successD", "Successfully deleted basket.");
        return "redirect:/basket";
    }
    @PostMapping(value = "/write_com")
    @PreAuthorize("isAuthenticated()")
    public String addCom(

            @RequestParam(name="comment") String comment,
            @RequestParam(name="item_id") Long item_id

    )
    {
        itemService.addComment(new Comments(null,comment,new Date(System.currentTimeMillis()),itemService.getItem(item_id),getUserData()));
        return "redirect:/details/"+ item_id;
    }
    @PostMapping(value = "/edcom")

    public String savecom(
            @RequestParam(name="item_id") Long item_id,
            @RequestParam(name="comment") String com,
            @RequestParam(name="id") Long id,
            @RequestParam(name="del", defaultValue = "0") int del

    )
    {
        Comments c = itemService.getComment(id);
        if(del!=1) {
            
            c.setComment(com);
            itemService.saveComment(c);
        }
        else {
            itemService.deleteComment(c);
        }

        return "redirect:/details/"+ item_id;
    }


}






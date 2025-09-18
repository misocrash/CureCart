package com.example.Meds.controller;
import com.example.Meds.entity.Cart;
import com.example.Meds.entity.User;
import com.example.Meds.service.CartService;
import com.example.Meds.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//POST   /cart/create           → Create a new cart
//GET    /cart/                 → Get all carts of all users
//GET    /cart/{id}             → Get cart by user Id
//PUT    /cart/update/{id}      → Update cart by cart id
//DELETE /cart/delete/{id}      → Delete cart by cart id

@RestController
@RequestMapping("/cart")
public class CartController {
    private final CartService cartService;
    private final UserService userService;
    public CartController(CartService cartService, UserService userService) {
        this.cartService = cartService;
        this.userService=userService;
    }


    //@PostMapping("/register")
    //    public ResponseEntity<?> register(@RequestBody UserRegisterRequestDTO request){
    //        userService.register(request);
    //        return new ResponseEntity<>("User registered!!", HttpStatus.CREATED);
    //    }

    @PostMapping("/{userId}")
    public void createCart(@PathVariable Integer userId) {
        User user = userService.findUserById(userId);
        cartService.createCartForUser(user);
    }

}

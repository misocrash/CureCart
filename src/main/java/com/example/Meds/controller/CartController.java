package com.example.Meds.controller;
import com.example.Meds.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

//POST   /cart/create           → Create a new cart
//GET    /cart/                 → Get all carts of all users
//GET    /cart/{id}             → Get cart by user Id
//PUT    /cart/update/{id}      → Update cart by cart id
//DELETE /cart/delete/{id}      → Delete cart by cart id

@RestController
@RequestMapping("/cart")
public class CartController {
    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    //@PostMapping("/register")
    //    public ResponseEntity<?> register(@RequestBody UserRegisterRequestDTO request){
    //        userService.register(request);
    //        return new ResponseEntity<>("User registered!!", HttpStatus.CREATED);
    //    }

    @PostMapping
    public ResponseEntity<?> createCart(@ResponseBody cart){
        return null;
    }

}

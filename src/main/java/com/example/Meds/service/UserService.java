package com.example.Meds.service;
import com.example.Meds.dto.MedicineDetailsDTO;
import com.example.Meds.dto.SingleOrderDetailsDTO;
import com.example.Meds.dto.UserRegisterRequestDTO;
import com.example.Meds.entity.*;
import com.example.Meds.repository.CartItemRepository;
import com.example.Meds.repository.OrderRepository;
import com.example.Meds.repository.UsersRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Service
public class UserService {

    private final UsersRepository usersRepository;
    private final CartService cartService;
    private final OrderRepository orderRepository;
    private final CartItemRepository cartItemRepository;

    public UserService(UsersRepository userRepository,CartService cartService, OrderRepository orderRepository, CartItemRepository cartItemRepository) {
        this.usersRepository = userRepository;
        this.cartService=cartService;
        this.orderRepository = orderRepository;
        this.cartItemRepository = cartItemRepository;
    }

    public void register(UserRegisterRequestDTO request) {

        User user = new User(
                request.getName(),
                request.getEmail(),
                request.getPassword(),
                Role.USER,
                LocalDateTime.now()
        );

        usersRepository.save(user);
        cartService.createCartForUser(user);

    }

    public List<User> findAllUsers() {
        return usersRepository.findAll();
    }

    public User findUserById(Integer id) {

        return usersRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public void deleteById(int id) {
        usersRepository.deleteById(id);
    }


    public User updateUser(int id, User updatedUser) {
        User optionalUser = usersRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));

        optionalUser.setName(updatedUser.getName());
        optionalUser.setEmail(updatedUser.getEmail());
        optionalUser.setPassword(updatedUser.getPassword());

        return usersRepository.save(optionalUser);
    }

    public List<SingleOrderDetailsDTO> getOrderHistory(int userId) {

//        OrderStatus orderStatus = OrderStatus.DELIVERED;

        User user = usersRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Order> orderList = orderRepository.findByUser(user);

        System.out.println(orderList);

        List<SingleOrderDetailsDTO> response = new ArrayList<>();

        for(Order order : orderList){
            Cart cart = order.getCart();
            List<CartItem> cartItemList = cartItemRepository.findByCart(cart);
            List<MedicineDetailsDTO> medicineDetailsDTOList = new ArrayList<>();
            for(CartItem cartItem: cartItemList){
                Medicine medicine = cartItem.getMedicine();
                MedicineDetailsDTO dto = new MedicineDetailsDTO(
                        medicine.getName(),
                        medicine.getPrice(),
                        medicine.getManufacture_name(),
                        cartItem.getQuantity(),
                        medicine.getPack_size()
                );
                medicineDetailsDTOList.add(dto);
            }
            SingleOrderDetailsDTO dto = new SingleOrderDetailsDTO(
                    order.getOrderId(),
                    medicineDetailsDTOList,
                    order.getStatus().toString(),
                    order.getTotalAmount()
            );
            response.add(dto);
        }

        return response;

    }
}
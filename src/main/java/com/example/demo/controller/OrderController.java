package com.example.demo.controller;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.execution.FlowExecutionOutcome;
import org.springframework.webflow.executor.FlowExecutionResult;
import org.springframework.webflow.executor.FlowExecutor;
import org.springframework.webflow.test.MockExternalContext;

import com.example.demo.dao.CustomerDaoImpl;
import com.example.demo.model.Cart;
import com.example.demo.model.Customer;
import com.example.demo.model.CustomerOrder;
import com.example.demo.service.CartItemService;
import com.example.demo.service.CartService;
import com.example.demo.service.CustomerOrderService;

@Controller
public class OrderController {

	@Autowired
	private CartService cartService;
	
	@Autowired
	private CartItemService cartItemService;

	@Autowired
	private CustomerOrderService customerOrderService;

	@Autowired
	private CustomerDaoImpl customerService;

	@GetMapping("/order/{cartId}")
	public String createOrder(@PathVariable("cartId") String cartId, Model model) {

		CustomerOrder customerOrder = new CustomerOrder();

		Cart cart = cartService.getCartByCartId(Integer.parseInt(cartId));

		Customer customer = cart.getCustomer();
		
		customerOrder.setCart(cart);

		customerOrder.setCustomer(customer);

		customerOrder.setShippingAddress(customer.getShippingAddress());

		customerOrder.setBillingAddress(customer.getBillingAddress());

		customerOrderService.addCustomerOrder(customerOrder);
	
		
		model.addAttribute(customerOrder);
	
	
		return "collectCustomerInfo";
	}
	
	
	@PostMapping("/order/ship/{cartId}/{customerOrderId}")
	public String getShipDetails(@PathVariable("cartId") String cartId,@PathVariable("customerOrderId") String customerOrderId,@ModelAttribute("customerOrder") CustomerOrder customerOrder, Model model){
		
		CustomerOrder existing = customerOrderService.getCustomerOrder(Integer.parseInt(customerOrderId));
	
		existing.getCustomer().setBillingAddress(customerOrder.getCustomer().getBillingAddress());
		
		
		customerOrderService.editCustomerOrder(existing);
		
		model.addAttribute(existing);
		return "collectShippingDetail";
		
	}
	
	@PostMapping("/order/confirm/{cartId}/{customerOrderId}")
	public String getConfirmDetails(@PathVariable("cartId") String cartId,@PathVariable("customerOrderId") String customerOrderId,@ModelAttribute("customerOrder") CustomerOrder customerOrder, Model model){
		
		CustomerOrder existing = customerOrderService.getCustomerOrder(Integer.parseInt(customerOrderId));
		
		System.out.println("customer given shipping Details --" + customerOrder.getCustomer().getShippingAddress().toString());
		
		existing.getCustomer().setShippingAddress(customerOrder.getCustomer().getShippingAddress());
		
		System.out.println("New set shipping Details --" + existing.getCustomer().getShippingAddress().toString());
		
		existing.getCart().setTotalPrice(customerOrderService.getCustomerOrderGrandTotal(Integer.parseInt(cartId)));
		
		customerOrderService.editCustomerOrder(existing);
		
		model.addAttribute(existing);
		
		return "orderConfirmation";
		
	}
	
	@PostMapping("/order/submit/{cartId}/{customerOrderId}")
	public String getSubmitDetails(@PathVariable("cartId") String cartId,@PathVariable("customerOrderId") String customerOrderId,@ModelAttribute("customerOrder") CustomerOrder customerOrder){
		
		Cart cart = cartService.getCartByCartId(Integer.parseInt(cartId));
		
		CustomerOrder existing = customerOrderService.getCustomerOrder(Integer.parseInt(customerOrderId));
		
		cartItemService.removeAllCartItems(cart);
		customerOrderService.delete(existing);
		
		
		//cartItemService.removeAllCartItems(cart);
		System.out.println("************cart Id is*******************" + cartId);
		
		
		return "thankCustomer";
	}
	

}

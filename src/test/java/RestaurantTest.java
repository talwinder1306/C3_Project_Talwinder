import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.*;

class RestaurantTest {
    Restaurant restaurant;


    /**
     * This method initializes a restaurant object
     * Since we need one object for running each test, we run this before each
     */
    @BeforeEach
    private void create_restaurant_object() {
        LocalTime openingTime = LocalTime.parse("10:30:00");
        LocalTime closingTime = LocalTime.parse("22:00:00");
        restaurant = new Restaurant("Amelie's cafe", "Chennai", openingTime, closingTime);
        restaurant.addToMenu("Sweet corn soup", 119);
        restaurant.addToMenu("Vegetable lasagne", 269);
    }

    //>>>>>>>>>>>>>>>>>>>>>>>>>OPEN/CLOSED<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    /**
     * This method uses a partial mocked restaurant object since we need to check the values present in the object,
     * to determine if the current time is between open and closing time.
     * Here, we are generating a Random time between the restaurant's opening and closing time.
     * We mock the call to getCurrentTime() to return this random number between the restaurant's opening and closing time.
     * And we expect the isRestaurantOpen() method to return true
     */
    @Test
    public void is_restaurant_open_should_return_true_if_time_is_between_opening_and_closing_time(){
        Restaurant mockRestaurant = Mockito.spy(restaurant);
        LocalTime betweenOpenAndClose = getRandomTimeBetween(mockRestaurant.openingTime, mockRestaurant.closingTime);

        Mockito.when(mockRestaurant.getCurrentTime()).thenReturn(betweenOpenAndClose);
        assertTrue(mockRestaurant.isRestaurantOpen());
    }

    /**
     *
     * @param lowerBound - the lower bound of time range
     * @param upperBound - the upper bound of time range
     * @return Random time between lower and upper bound
     */
    private LocalTime getRandomTimeBetween(LocalTime lowerBound, LocalTime upperBound) {
        return LocalTime.ofSecondOfDay(ThreadLocalRandom
                .current()
                .nextInt(lowerBound.toSecondOfDay(), upperBound.toSecondOfDay()));
    }
    /**
     * This method uses a partial mocked restaurant object since we need to check the values present in the object,
     * to determine if the current time is outside open and closing time.
     * Here, we are generating a Random time between the restaurant's closing and opening time i.e. the outside hours.
     * We mock the call to getCurrentTime() to return this random number between the restaurant's outside hours.
     * And we expect the isRestaurantOpen() method to return false
     */
    @Test
    public void is_restaurant_open_should_return_false_if_time_is_outside_opening_and_closing_time(){
        Restaurant mockRestaurant = Mockito.spy(restaurant);

        LocalTime betweenMidnightAndOpen = getRandomTimeBetween(LocalTime.MIDNIGHT, mockRestaurant.openingTime);
        LocalTime betweenCloseAndMidnight = getRandomTimeBetween(mockRestaurant.closingTime, LocalTime.parse("23:59:59"));
        LocalTime betweenCloseAndOpen = Math.random() < 0.5 ? betweenMidnightAndOpen : betweenCloseAndMidnight;

        Mockito.when(mockRestaurant.getCurrentTime()).thenReturn(betweenCloseAndOpen);

        assertFalse(mockRestaurant.isRestaurantOpen());
    }

    //<<<<<<<<<<<<<<<<<<<<<<<<<OPEN/CLOSED>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>


    //>>>>>>>>>>>>>>>>>>>>>>>>>>>MENU<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    @Test
    public void adding_item_to_menu_should_increase_menu_size_by_1(){
        int initialMenuSize = restaurant.getMenu().size();
        restaurant.addToMenu("Sizzling brownie",319);
        assertEquals(initialMenuSize+1,restaurant.getMenu().size());
    }

    @Test
    public void removing_item_from_menu_should_decrease_menu_size_by_1() throws itemNotFoundException {
        int initialMenuSize = restaurant.getMenu().size();
        restaurant.removeFromMenu("Vegetable lasagne");
        assertEquals(initialMenuSize-1,restaurant.getMenu().size());
    }
    @Test
    public void removing_item_that_does_not_exist_should_throw_exception() {
        assertThrows(itemNotFoundException.class,
                ()->restaurant.removeFromMenu("French fries"));
    }
    //<<<<<<<<<<<<<<<<<<<<<<<MENU>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    //<<<<<<<<<<<<<<<<<<<<<<<ORDER>>>>>>>>>>>>>>>>>>>>>>>>>>>>

    /**
     * We need to calculate the total cost of menu items selected
     * We will pass the items name as string to the method
     * the method returns the order amount which should be a int.
     * No need to save selected items
     */

    @Test
    public void get_order_total_should_return_0_if_list_is_empty() {
        List<String> itemNames = new ArrayList<>();
        assertEquals(0, restaurant.getOrderTotal(itemNames));
    }

    @Test
    public void get_order_total_should_return_388_if_first_and_second_item_selected() {
        restaurant.addToMenu("Sizzling Brownie", 319);
        restaurant.addToMenu("Alfredo Pasta", 249);
        List<String> itemNames = new ArrayList<>();
        itemNames.add("Sweet corn soup");
        itemNames.add("Vegetable lasagne");
        int expectedOrderTotal = restaurant.getMenu().get(0).getPrice() + restaurant.getMenu().get(1).getPrice();
        assertEquals(expectedOrderTotal, restaurant.getOrderTotal(itemNames));
    }

    //<<<<<<<<<<<<<<<<<<<<<<<ORDER>>>>>>>>>>>>>>>>>>>>>>>>>>>>


}
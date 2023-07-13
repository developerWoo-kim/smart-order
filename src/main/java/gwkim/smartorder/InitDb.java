package gwkim.smartorder;

import gwkim.smartorder.item.domain.Item;
import gwkim.smartorder.item.repository.ItemRepository;
import gwkim.smartorder.member.domain.Member;
import gwkim.smartorder.member.repository.MemberRepository;
import gwkim.smartorder.option.domain.Option;
import gwkim.smartorder.option.domain.OptionDetail;
import gwkim.smartorder.option.repository.OptionDetailRepository;
import gwkim.smartorder.option.repository.OptionRepository;
import gwkim.smartorder.order.domain.Cart;
import gwkim.smartorder.order.domain.CartItem;
import gwkim.smartorder.order.repository.CartRepository;
import gwkim.smartorder.order.service.CartService;
import gwkim.smartorder.order.service.OrderService;
import gwkim.smartorder.store.domain.Store;
import gwkim.smartorder.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class InitDb {
    private final InitService initService;

    @PostConstruct
    public void init() {
        initService.dbInit1();
        initService.dbInti_addCartItem1();
        initService.dbInti_addCartItem2();
        initService.dbInti_addCartItem3();
        initService.order1();

        initService.dbInit2();
        initService.dbInt2_addCartItem1();
        initService.dbInt2_addCartItem2();
        initService.order2();
    }

    @Component
    @Transactional
    @RequiredArgsConstructor
    static class InitService {
        private final StoreRepository storeRepository;
        private final ItemRepository itemRepository;
        private final OptionRepository optionRepository;
        private final OrderService orderService;
        private final MemberRepository memberRepository;
        private final OptionDetailRepository optionDetailRepository;
        private final CartRepository cartRepository;

        public void dbInit1() {
            // 회원 생성
            memberRepository.save(new Member().builder().name("홍길동").build());

            // 매장 생성
            Store saveStore = storeRepository.save(new Store()
                    .builder()
                    .storeName("건우 카페")
                    .address("대전시 중구 ")
                    .phone("070-0000-0000")
                    .minOrderPrice(5000)
                    .minPickupTime(10)
                    .maxPickupTime(30)
                    .status("Y").build());

            Item saveItem1 = itemRepository.save(createItem(saveStore, "아메리카노", 2000, "N", "1001", "Y"));

            List<OptionDetail> optionDetailList1 = new ArrayList<>();
            optionDetailList1.add(getOptionDetail("소", 0, 1));
            optionDetailList1.add(getOptionDetail("중", 1500, 2));
            optionDetailList1.add(getOptionDetail("대", 2500, 3));

            optionRepository.save(Option.createOption(saveItem1, "사이즈1", "Y", 1,
                    1, 1, optionDetailList1.toArray(new OptionDetail[optionDetailList1.size()])));

            List<OptionDetail> optionDetailList2 = new ArrayList<>();
            optionDetailList2.add(getOptionDetail("조금", 0, 1));
            optionDetailList2.add(getOptionDetail("많이", 0, 2));

            optionRepository.save(Option.createOption(saveItem1, "얼음1", "N", 0,
                    0, 2, optionDetailList2.toArray(new OptionDetail[optionDetailList2.size()])));


            Item saveItem2 = itemRepository.save(createItem(saveStore, "카페라떼", 4500, "P", "1001", "Y"));

            List<OptionDetail> optionDetailList3 = new ArrayList<>();
            optionDetailList3.add(getOptionDetail("소", 0, 1));
            optionDetailList3.add(getOptionDetail("중", 2500, 2));
            optionDetailList3.add(getOptionDetail("대", 3500, 3));

            optionRepository.save(Option.createOption(saveItem2, "사이즈", "Y", 1,
                    1, 1, optionDetailList3.toArray(new OptionDetail[optionDetailList3.size()])));

            List<OptionDetail> optionDetailList4 = new ArrayList<>();
            optionDetailList4.add(getOptionDetail("조금", 0, 1));
            optionDetailList4.add(getOptionDetail("많이", 0, 2));

            optionRepository.save(Option.createOption(saveItem2, "얼음", "N", 0,
                    0, 2, optionDetailList4.toArray(new OptionDetail[optionDetailList4.size()])));

            List<OptionDetail> optionDetailList5 = new ArrayList<>();
            optionDetailList5.add(getOptionDetail("2샷", 500, 1));
            optionDetailList5.add(getOptionDetail("3샷", 1000, 2));
            optionDetailList5.add(getOptionDetail("4샷", 1500, 3));

            optionRepository.save(Option.createOption(saveItem2, "샷 추가", "N", 0,
                    0, 3, optionDetailList5.toArray(new OptionDetail[optionDetailList5.size()])));


            Item saveItem3 = itemRepository.save(createItem(saveStore, "망고 에이드", 5500, "R", "1001", "Y"));
            List<OptionDetail> optionDetailList6 = new ArrayList<>();
            optionDetailList6.add(getOptionDetail("소", 1000, 1));
            optionDetailList6.add(getOptionDetail("중", 1500, 2));
            optionDetailList6.add(getOptionDetail("대", 2000, 3));

            optionRepository.save(Option.createOption(saveItem3, "사이즈", "N", 0,
                    0, 1, optionDetailList6.toArray(new OptionDetail[optionDetailList6.size()])));

        }

        public void dbInti_addCartItem1() {
            // 회원 선택
            Member member = memberRepository.findById(1L).get();
            // 제품 선택´
            Item item = itemRepository.findById(2L).get();
            // 옵션 생성
            List<OptionDetail> optionDetailList = new ArrayList<>();
            optionDetailList.add(optionDetailRepository.findById(7L).get());
            optionDetailList.add(optionDetailRepository.findById(11L).get());
            // 상품 생성
            CartItem cartItem = CartItem.createCartItem(item, 2, optionDetailList.toArray(new OptionDetail[optionDetailList.size()]));
            // 장바구니 생성
            Cart cart = Cart.createCart(member, cartItem, item.getStore().getId());
            Cart saveCart = cartRepository.save(cart);
        }

        public void dbInti_addCartItem2() {
            // 회원 선택
            Cart cart = cartRepository.findById(1L).get();
            // 제품 선택´
            Item item = itemRepository.findById(1L).get();
            // 옵션 생성
            List<OptionDetail> optionDetailList = new ArrayList<>();
            optionDetailList.add(optionDetailRepository.findById(1L).get());
            // 상품 생성
            CartItem cartItem = CartItem.createCartItem(item, 1, optionDetailList.toArray(new OptionDetail[optionDetailList.size()]));
            // 장바구니 생성
            cart.addCartItem(cartItem);
        }

        public void dbInti_addCartItem3() {
            // 회원 선택
            Cart cart = cartRepository.findById(1L).get();
            // 제품 선택´
            Item item = itemRepository.findById(3L).get();
            // 옵션 생성
            List<OptionDetail> optionDetailList = new ArrayList<>();
            // 상품 생성
            CartItem cartItem = CartItem.createCartItem(item, 1, optionDetailList.toArray(new OptionDetail[optionDetailList.size()]));
            // 장바구니 생성
            cart.addCartItem(cartItem);
        }

        public void dbInit2() {
            // 매장 생성
            Store saveStore = storeRepository.save(new Store()
                    .builder()
                    .storeName("족발집")
                    .address("대전시 봉명동")
                    .phone("070-0000-0000")
                    .minOrderPrice(10000)
                    .minPickupTime(10)
                    .maxPickupTime(30)
                    .status("Y").build());

            Item saveItem1 = itemRepository.save(createItem(saveStore, "기본족발", 20000, "Y", "2001", "Y"));

            List<OptionDetail> optionDetailList1 = new ArrayList<>();
            optionDetailList1.add(getOptionDetail("소", 0, 1));
            optionDetailList1.add(getOptionDetail("중", 14000, 2));
            optionDetailList1.add(getOptionDetail("대", 24000, 3));

            optionRepository.save(Option.createOption(saveItem1, "사이즈", "Y", 1,
                    1, 1, optionDetailList1.toArray(new OptionDetail[optionDetailList1.size()])));

            List<OptionDetail> optionDetailList2 = new ArrayList<>();
            optionDetailList2.add(getOptionDetail("쌈야채", 2500, 1));
            optionDetailList2.add(getOptionDetail("겉절이세트", 2500, 2));
            optionDetailList2.add(getOptionDetail("고추절임지", 1000, 2));

            optionRepository.save(Option.createOption(saveItem1, "반찬류", "N", 0,
                    2, 2, optionDetailList2.toArray(new OptionDetail[optionDetailList2.size()])));


            Item saveItem2 = itemRepository.save(createItem(saveStore, "가족의족보", 34000, "P", "2001", "Y"));

            List<OptionDetail> optionDetailList3 = new ArrayList<>();
            optionDetailList3.add(getOptionDetail("소 (2인분)", 0, 1));
            optionDetailList3.add(getOptionDetail("중 (2~3인분)", 39000, 2));
            optionDetailList3.add(getOptionDetail("대 4인분)", 50000, 3));

            optionRepository.save(Option.createOption(saveItem2, "사이즈", "Y", 1,
                    1, 1, optionDetailList3.toArray(new OptionDetail[optionDetailList3.size()])));

            List<OptionDetail> optionDetailList4 = new ArrayList<>();
            optionDetailList4.add(getOptionDetail("불족발 소스", 700, 1));
            optionDetailList4.add(getOptionDetail("허미마늘 소스", 700, 2));
            optionDetailList4.add(getOptionDetail("흑마늘 소스", 700, 3));
            optionDetailList4.add(getOptionDetail("바베큐 소스", 700, 3));

            optionRepository.save(Option.createOption(saveItem2, "소스류", "N", 0,
                    4, 2, optionDetailList4.toArray(new OptionDetail[optionDetailList4.size()])));

            List<OptionDetail> optionDetailList5 = new ArrayList<>();
            optionDetailList5.add(getOptionDetail("앞발 변경", 4000, 1));
            optionDetailList5.add(getOptionDetail("직화불보쌈 추가", 5000, 2));
            optionDetailList5.add(getOptionDetail("미니족 추가", 5500, 3));

            optionRepository.save(Option.createOption(saveItem2, "고기 추가", "N", 0,
                    2, 3, optionDetailList5.toArray(new OptionDetail[optionDetailList5.size()])));

        }

        public void dbInt2_addCartItem1() {
            // 회원 선택
            Member member = memberRepository.findById(1L).get();
            // 제품 선택´
            Item item = itemRepository.findById(4L).get();
            // 옵션 생성
            List<OptionDetail> optionDetailList = new ArrayList<>();
            optionDetailList.add(optionDetailRepository.findById(17L).get());
            // 상품 생성
            CartItem cartItem = CartItem.createCartItem(item, 1, optionDetailList.toArray(new OptionDetail[optionDetailList.size()]));
            // 장바구니 생성
            Cart cart = Cart.createCart(member, cartItem, item.getStore().getId());
            cartRepository.save(cart);
        }

        public void dbInt2_addCartItem2() {
            Cart cart = cartRepository.findById(2L).get();
            // 제품 선택´
            Item item = itemRepository.findById(5L).get();
            // 옵션 생성
            List<OptionDetail> optionDetailList = new ArrayList<>();
            optionDetailList.add(optionDetailRepository.findById(23L).get());
            optionDetailList.add(optionDetailRepository.findById(26L).get());
            optionDetailList.add(optionDetailRepository.findById(31L).get());
            // 상품 생성
            CartItem cartItem = CartItem.createCartItem(item, 1, optionDetailList.toArray(new OptionDetail[optionDetailList.size()]));
            // 장바구니 생성
            cart.addCartItem(cartItem);
        }

        public void order1() {
            Long cartId = 1L;
            orderService.order(cartId);
            cartRepository.deleteById(1L);
        }

        public void order2() {
            Long cartId = 1L;
            orderService.order(cartId);
        }

        /**
         * 상품 생성
         *
         * @param saveStore
         * @param name
         * @param price
         * @param rating
         * @param category
         * @param status
         * @return
         */
        private Item createItem(Store saveStore, String name, int price, String rating, String category, String status) {
            return new Item()
                    .builder()
                    .store(saveStore)
                    .itemName(name)
                    .price(price)
                    .ratingStatus(rating)
                    .category(category)
                    .status(status).build();
        }

        /**
         * 상세 옵션 생성
         *
         * @param name
         * @param price
         * @param orders
         * @return
         */
        private OptionDetail getOptionDetail(String name, int price, int orders) {
            return new OptionDetail()
                    .builder()
                    .name(name)
                    .price(price)
                    .orders(orders).build();
        }

    }
}

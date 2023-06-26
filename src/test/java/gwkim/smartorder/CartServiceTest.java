package gwkim.smartorder;

import gwkim.smartorder.item.domain.Item;
import gwkim.smartorder.item.repository.ItemRepository;
import gwkim.smartorder.member.domain.Member;
import gwkim.smartorder.member.repository.MemberRepository;
import gwkim.smartorder.option.domain.OptionDetail;
import gwkim.smartorder.option.repository.OptionDetailRepository;
import gwkim.smartorder.order.domain.Cart;
import gwkim.smartorder.order.domain.CartItem;
import gwkim.smartorder.order.domain.option.CartOption;
import gwkim.smartorder.order.repository.CartRepository;
import gwkim.smartorder.order.repository.OrderRepository;
import gwkim.smartorder.store.domain.Store;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@Transactional(readOnly = true)
public class CartServiceTest {
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    CartRepository cartRepository;
    @Autowired
    OptionDetailRepository optionDetailRepository;

    @Test
    @DisplayName("장바구니 연관관계 테스트")
    @Transactional(readOnly = false)
    @Rollback(value = false)
    public void cartRelationTest() throws Exception {
        //given
        // 회원
        Member member = memberRepository.findById(1L).get();
        // 제품 선택´
        Item selectItem = itemRepository.findById(2L).get();
        // 옵션 생성
        List<OptionDetail> optionDetailList = new ArrayList<>();
//        optionDetailList.add(optionDetailRepository.findById(7L).get());
//        optionDetailList.add(optionDetailRepository.findById(11L).get());

        // 상품 생성
        CartItem cartItem = CartItem.createCartItem(selectItem, 1, optionDetailList.toArray(new OptionDetail[optionDetailList.size()]));
        // 장바구니 생성
        Cart cart = cartRepository.findCart(member.getId())
                .orElseGet(() -> Cart.createCart(member, cartItem));

        if(cart != null) {
            Store store = cart.getCartItemList().stream()
                    .map(CartItem::getItem)
                    .map(Item::getStore)
                    .findFirst()
                    .orElse(null);

            // 같은 매장의 주문건 일 경우
            boolean allMatch = false;
            if(store.getId().equals(selectItem.getStore())) {
                    List<Long> cartOptionIds = cart.getCartItemList().stream()
                            .filter(cartI -> cartI.getItem().getId().equals(selectItem.getId()))
                            .flatMap(cartI -> Optional.ofNullable(cartI.getCartOptions()).orElse(Collections.emptyList()).stream())
                            .map(cartOption -> cartOption.getOptionDetail().getId())
                            .sorted()
                            .collect(Collectors.toList());

                    // 옵션이 없는 상품만 있다면 장바구니에 바로 담기
                        List<Long> sortedOptionDetailIds = optionDetailList.stream()
                                .map(OptionDetail::getId)
                                .sorted()
                                .collect(Collectors.toList());

                        allMatch = cartOptionIds.equals(sortedOptionDetailIds);
                        assertThat(allMatch).isTrue();
            }
        }


        Cart saveCart = cartRepository.save(cart);
        //when
        Cart findCart = cartRepository.findById(saveCart.getId()).get();
        //then
        assertThat(findCart).isNotNull();
        assertThat(findCart.getMember()).isNotNull();
        assertThat(findCart.getCartItemList()).isNotNull();
    }

    @Test
    @DisplayName("주문 상품의 옵션에 따른 가격 검증 테스트")
    @Rollback(value = false)
    public void optionPriceVerification() throws Exception {
        //given
        int itemCount = 2;
        int itemPrice = 0;
        int optionPrice = 0;
        int totalPrice = 0;
        // 회원
        Member member = memberRepository.findById(1L).get();
        // 제품 선택´
        Item item = itemRepository.findById(2L).get();
        itemPrice = item.getPrice();
        // 옵션 생성
        List<OptionDetail> optionDetailList = new ArrayList<>();
        OptionDetail optionDetail1 = optionDetailRepository.findById(7L).get();
        optionPrice += optionDetail1.getPrice();
        OptionDetail optionDetail2 = optionDetailRepository.findById(11L).get();
        optionPrice += optionDetail2.getPrice();
        optionDetailList.add(optionDetail1);
        optionDetailList.add(optionDetail2);
        //when
        // 상품 생성
        CartItem cartItem = CartItem.createCartItem(item, itemCount, optionDetailList.toArray(new OptionDetail[optionDetailList.size()]));
        // 장바구니 생성
        Cart cart = Cart.createCart(member, cartItem);

        Cart saveCart = cartRepository.save(cart);
        //when
        Cart findCart = cartRepository.findById(saveCart.getId()).get();
        //then
        totalPrice = (itemPrice + optionPrice) * itemCount;
        assertThat(findCart.getCartItemList().get(0).getCartItemPrice()).isEqualTo(totalPrice);
    }

    @Test
    @DisplayName("동일한 옵션의 상품을 선택했을 경우의 검증 테스트")
    public void sameOptionVerification() throws Exception {
        //given
        // 회원 선택
        Member member = memberRepository.findById(1L).get();
        // 제품 선택´
        Item item = itemRepository.findById(2L).get();
        // 옵션 생성
        List<OptionDetail> optionDetailList = new ArrayList<>();
        optionDetailList.add(optionDetailRepository.findById(7L).get());
        optionDetailList.add(optionDetailRepository.findById(11L).get());
        // 상품 생성
        CartItem cartItem = CartItem.createCartItem(item, 1, optionDetailList.toArray(new OptionDetail[optionDetailList.size()]));
        // 장바구니 생성
        Cart cart = Cart.createCart(member, cartItem);
        Cart saveCart = cartRepository.save(cart);
        //when
        // 회원 선택
        Member member2 = memberRepository.findById(1L).get();
        // 제품 선택´
        Item item2 = itemRepository.findById(2L).get();
        // 옵션 생성
        List<OptionDetail> optionDetailList2 = new ArrayList<>();
        optionDetailList2.add(optionDetailRepository.findById(7L).get());
        optionDetailList2.add(optionDetailRepository.findById(11L).get());
        // 상품 생성
        // 장바구니에 상품중에 같은 상품이 있는지 확인

        // -> 같은 상품 아이디가 없는 경우 장바구니 상품 생성
        //   -> create
        // -> 같은 상품 아이디가 있는 경우 옵션 확인
        //   -> 같은 옵션이 있는 경우 수량만 추가 (변경감지 사용)
        //   -> 같은 옵션이 아닌 경우 장바구니 상품 생성


//        CartItem cartItem = CartItem.createCartItem(item, 1, optionDetailList.toArray(new OptionDetail[optionDetailList.size()]));


        //then
    }

    @Test
    @DisplayName("생성된 장바구니가 있는지 테스트")
    public void cartFindTest() throws Exception {
        //given
        Item findItem = itemRepository.findById(2L).get();
        // 회원 선택
        Member member = memberRepository.findById(1L).get();
        // 옵션 생성
        List<OptionDetail> optionDetailList = new ArrayList<>();
        optionDetailList.add(optionDetailRepository.findById(7L).get());
        optionDetailList.add(optionDetailRepository.findById(12L).get());

        System.out.println("========= CART 조회 시작 =============");
        Optional<Cart> findCart = cartRepository.findCart(1L);
//        Cart cart = findCart.get();
        System.out.println("========= CART 조회 끝 =============");
//        System.out.println(findCart.get().getCartItemList().size());

        boolean allMatch = false;
        if(findCart.isPresent()) {
            List<Long> cartOptionIds = findCart.get().getCartItemList().stream()
                    .flatMap(cartItem -> Optional.ofNullable(cartItem.getCartOptions()).orElse(Collections.emptyList()).stream())
                    .map(cartOption -> cartOption.getOptionDetail().getId())
                    .sorted()
                    .collect(Collectors.toList());

            // 옵션이 없는 상품만 있다면 장바구니에 바로 담기
            if(cartOptionIds.isEmpty()) {
                assertThat(allMatch).isFalse();
            } else {
                List<Long> sortedOptionDetailIds = optionDetailList.stream()
                        .map(OptionDetail::getId)
                        .sorted()
                        .collect(Collectors.toList());

                allMatch = cartOptionIds.equals(sortedOptionDetailIds);
                assertThat(allMatch).isTrue();
            }
        }

        //when
        //then
    }
    @Test
    @DisplayName("동일한 제품 확인")
    @Transactional
    @Rollback(value = false)
    public void 동일한_제품_확인_테스트() throws Exception {
        //given
        // 회원
        Member member = memberRepository.findById(1L).get();
        // 제품 선택´
        Item selectItem = itemRepository.findById(2L).get();
        // 옵션 생성
        List<OptionDetail> optionDetailList = new ArrayList<>();
        optionDetailList.add(optionDetailRepository.findById(7L).get());
        optionDetailList.add(optionDetailRepository.findById(11L).get());

        // 상품 생성
        CartItem cartItem = CartItem.createCartItem(selectItem, 1, optionDetailList.toArray(new OptionDetail[optionDetailList.size()]));
        // 장바구니가 기존에 없는 경우 바로 생성
        Cart cart = cartRepository.findCart(member.getId())
                .orElseGet(() -> cartRepository.save(Cart.createCart(member, cartItem)));
        // 장바구니가 있는 경우 매장을 비교한다.
        if(cart != null) {
            Store store = cart.getCartItemList().stream()
                    .map(CartItem::getItem)
                    .map(Item::getStore)
                    .findFirst()
                    .orElseThrow();
            // 동일한 매장인지 비교
            if (store.getId().equals(selectItem.getStore().getId())) {
                // 비즈니스 로직 수행
                // 동일한 제품이 있는지?
                List<CartItem> dupleItemList = cart.getCartItemList().stream()
                        .filter(findCartItem -> findCartItem.getItem().getId().equals(selectItem.getId())) // 선택한 상품 아이디 확인
                        .collect(Collectors.toList());
                assertThat(dupleItemList).isNotEmpty();

                // 선택한 상품이 장바구니에 있는 경우
                if(!dupleItemList.isEmpty()) {
                    // 옵션이 없는 상품이 존재할 경우 카운트 추가
                    if(optionDetailList.isEmpty()) {
                        for (CartItem item : dupleItemList) {
                            if(item.getCartOptions().isEmpty()) {
                                item.addCartItemCount(cartItem.getCount(), cartItem.getCartItemPrice());
                            }
                        }
                    } else {
                        boolean isAllMatch = false;
                        // 동일한 옵션이 존재하는지 확인
                        for (CartItem item : dupleItemList) {
                            List<Long> cartOptionIds = item.getCartOptions().stream()
                                    .map(cartOption -> cartOption.getOptionDetail().getId())
                                    .sorted()
                                    .collect(Collectors.toList());
                            System.out.println(cartOptionIds);

                            List<Long> sortedOptionDetailIds = optionDetailList.stream()
                                .map(OptionDetail::getId)
                                .sorted()
                                .collect(Collectors.toList());

                            isAllMatch = cartOptionIds.equals(sortedOptionDetailIds);
                            // 동일한 옵션이 존재할 경우
                            if(isAllMatch) {
                                // 기존 제품에 가격, 수량 추가
                                item.addCartItemCount(cartItem.getCount(), cartItem.getCartItemPrice());
                                break;
                            }
                        }
                        // 동일한 옵션이 존재하지 않을 경우
                        if(!isAllMatch) {
                            // 새롭게 추가
                            cart.addCartItem(cartItem);
                            cart.addMember(member);
                        }
                    }
                }

            }
        }
    }

    @Test
    @DisplayName("장바구니에 동일한 제품중 옵션이 없는 제품 확인 테스트")
    public void 장바구니에_동일한_제품중_옵션이_없는_제품확인_테스트() throws Exception {
        //given
        // 회원
        Member member = memberRepository.findById(1L).get();
        // 제품 선택´
        Item selectItem = itemRepository.findById(2L).get();
        // 옵션 생성
        List<OptionDetail> optionDetailList = new ArrayList<>();
        optionDetailList.add(optionDetailRepository.findById(7L).get());
        optionDetailList.add(optionDetailRepository.findById(12L).get());

        // 상품 생성
        CartItem cartItem = CartItem.createCartItem(selectItem, 1, optionDetailList.toArray(new OptionDetail[optionDetailList.size()]));
        // 장바구니가 기존에 없는 경우 바로 생성
        Cart cart = cartRepository.findCart(member.getId())
                .orElseGet(() -> cartRepository.save(Cart.createCart(member, cartItem)));
        // 장바구니가 있는 경우 매장을 비교한다.
        if(cart != null) {
            Store store = cart.getCartItemList().stream()
                    .map(CartItem::getItem)
                    .map(Item::getStore)
                    .findFirst()
                    .orElseThrow();
            // 동일한 매장인지 비교
            if (store.getId().equals(selectItem.getStore().getId())) {
                // 비즈니스 로직 수행
                // 동일한 제품이 있는지?
                List<CartItem> collect = cart.getCartItemList().stream()
                        .filter(findCartItem -> findCartItem.getItem().getId().equals(selectItem.getId())) // 선택한 상품 아이디 확인
                        .filter(s -> !s.getCartOptions().isEmpty()) // 옵션이 없는 상품은 제외
                        .collect(Collectors.toList());

                assertThat(collect.size()).isEqualTo(2);
            }
        }
        //when
        //then
    }

    @Test
    @DisplayName("동일한 제품 중 옵션을 비교하는 테스트")
    public void 동일한_제품_중_옵션을_비교하는_테스트() throws Exception {
        //given
        // 회원
        Member member = memberRepository.findById(1L).get();
        // 제품 선택´
        Item selectItem = itemRepository.findById(2L).get();
        // 옵션 생성
        List<OptionDetail> optionDetailList = new ArrayList<>();
        optionDetailList.add(optionDetailRepository.findById(7L).get());
        optionDetailList.add(optionDetailRepository.findById(12L).get());

        // 상품 생성
        CartItem cartItem = CartItem.createCartItem(selectItem, 1, optionDetailList.toArray(new OptionDetail[optionDetailList.size()]));
        // 장바구니가 기존에 없는 경우 바로 생성
        Cart cart = cartRepository.findCart(member.getId())
                .orElseGet(() -> cartRepository.save(Cart.createCart(member, cartItem)));
        // 장바구니가 있는 경우 매장을 비교한다.
        if(cart != null) {
            Store store = cart.getCartItemList().stream()
                    .map(CartItem::getItem)
                    .map(Item::getStore)
                    .findFirst()
                    .orElseThrow();
            // 동일한 매장인지 비교
            if (store.getId().equals(selectItem.getStore().getId())) {
                // 비즈니스 로직 수행
                // 동일한 제품이 있는지?
                List<CartItem> dupleItemList = cart.getCartItemList().stream()
                        .filter(findCartItem -> findCartItem.getItem().getId().equals(selectItem.getId())) // 선택한 상품 아이디 확인
                        .filter(s -> !s.getCartOptions().isEmpty()) // 옵션이 없는 상품은 제외
                        .collect(Collectors.toList());

                boolean allMatch = false;
                for (CartItem item : dupleItemList) {
                    List<Long> cartOptionIds = item.getCartOptions().stream()
                            .map(cartOption -> cartOption.getOptionDetail().getId())
                            .sorted()
                            .collect(Collectors.toList());
                    System.out.println(cartOptionIds);

//                    List<Long> cartOptionIds = cart.getCartItemList().stream()
//                                .filter(cartI -> cartI.getItem().getId().equals(selectItem.getId()))
//                                .flatMap(cartI -> Optional.ofNullable(cartI.getCartOptions()).orElse(Collections.emptyList()).stream())
//                                .map(cartOption -> cartOption.getOptionDetail().getId())
//                                .sorted()
//                                .collect(Collectors.toList());

                        // 옵션이 없는 상품만 있다면 장바구니에 바로 담기

//                        List<Long> sortedOptionDetailIds = optionDetailList.stream()
//                                .map(OptionDetail::getId)
//                                .sorted()
//                                .collect(Collectors.toList());
//
//                        allMatch = cartOptionIds.equals(sortedOptionDetailIds);
//                        assertThat(allMatch).isTrue();

                }

            }
        }
        //when
        //then
    }
    @Test
    @DisplayName("장바구니가 존재하는 경우 매장을 비교하여 추가하는 테스트")
    public void 장바구니가_존재하는_경우_매장을_비교하여_추가하는_테스트() throws Exception {
    //given
        // 회원
        Member member = memberRepository.findById(1L).get();
        // 제품 선택´
        Item selectItem = itemRepository.findById(2L).get();
        // 옵션 생성
        List<OptionDetail> optionDetailList = new ArrayList<>();
        optionDetailList.add(optionDetailRepository.findById(7L).get());
        optionDetailList.add(optionDetailRepository.findById(12L).get());

        // 상품 생성
        CartItem cartItem = CartItem.createCartItem(selectItem, 1, optionDetailList.toArray(new OptionDetail[optionDetailList.size()]));
        // 장바구니가 기존에 없는 경우 바로 생성
        Cart cart = cartRepository.findCart(2L)
                .orElseGet(() -> cartRepository.save(Cart.createCart(member, cartItem)));
        // 장바구니가 있는 경우 매장을 비교한다.
        if(cart != null) {
            Store store = cart.getCartItemList().stream()
                    .map(CartItem::getItem)
                    .map(Item::getStore)
                    .findFirst()
                    .orElseThrow();
            // 동일한 매장인지 비교
            if(store.getId().equals(selectItem.getStore().getId())) {
                // 비즈니스 로직 수행
                  // 동일한 제품이 있는지?
                cart.getCartItemList().stream()
                        .filter(findCartItem -> findCartItem.getItem().getId().equals(selectItem.getId()))
                        .filter(s -> !s.getCartOptions().isEmpty())
                        .forEach(i -> {

                        });
                    // 동일한 제품중에 동일한 옵션이 있는지?

            } else {
                // 제거하고 추가할지
            }
        }
    }

    @Test
    @DisplayName("같은 상품의 다른 옵션일 경우 장바구니에 새롭게 담기는 테스트 ")
    public void 같은상품의_다른옵션의_경우_새롭게_추가되는_테스트() throws Exception {
//given
        // 회원
        Member member = memberRepository.findById(1L).get();
        // 제품 선택´
        Item selectItem = itemRepository.findById(2L).get();
        // 옵션 생성
        List<OptionDetail> optionDetailList = new ArrayList<>();
        optionDetailList.add(optionDetailRepository.findById(7L).get());
        optionDetailList.add(optionDetailRepository.findById(12L).get());

        // 상품 생성
        CartItem cartItem = CartItem.createCartItem(selectItem, 1, optionDetailList.toArray(new OptionDetail[optionDetailList.size()]));
        // 장바구니 생성
        Cart cart = cartRepository.findCart(member.getId())
                .orElseGet(() -> Cart.createCart(member, cartItem));

        if(cart != null) {
            Store store = cart.getCartItemList().stream()
                    .map(CartItem::getItem)
                    .map(Item::getStore)
                    .findFirst()
                    .orElse(null);

            // 같은 매장의 주문건 일 경우
            boolean allMatch = false;
            if(store.getId().equals(selectItem.getStore())) {
                List<Long> cartOptionIds = cart.getCartItemList().stream()
                        .filter(cartI -> cartI.getItem().getId().equals(selectItem.getId()))
                        .flatMap(cartI -> Optional.ofNullable(cartI.getCartOptions()).orElse(Collections.emptyList()).stream())
                        .map(cartOption -> cartOption.getOptionDetail().getId())
                        .sorted()
                        .collect(Collectors.toList());

                // 옵션이 없는 상품만 있다면 장바구니에 바로 담기
                List<Long> sortedOptionDetailIds = optionDetailList.stream()
                        .map(OptionDetail::getId)
                        .sorted()
                        .collect(Collectors.toList());

                allMatch = cartOptionIds.equals(sortedOptionDetailIds);
                assertThat(allMatch).isTrue();
            }
        }
    }


}

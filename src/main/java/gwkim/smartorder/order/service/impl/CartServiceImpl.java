package gwkim.smartorder.order.service.impl;

import gwkim.smartorder.item.domain.Item;
import gwkim.smartorder.item.repository.ItemRepository;
import gwkim.smartorder.member.domain.Member;
import gwkim.smartorder.member.repository.MemberRepository;
import gwkim.smartorder.option.domain.OptionDetail;
import gwkim.smartorder.option.repository.OptionDetailRepository;
import gwkim.smartorder.order.domain.Cart;
import gwkim.smartorder.order.domain.CartItem;
import gwkim.smartorder.order.repository.CartRepository;
import gwkim.smartorder.order.response.CartCommonResponse;
import gwkim.smartorder.order.service.CartService;
import gwkim.smartorder.store.domain.Store;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;
    private final CartRepository cartRepository;
    private final OptionDetailRepository optionDetailRepository;

    @Override
    @Transactional(readOnly = false)
    public CartCommonResponse<Cart> addCartItem(Long memberId, Long itemId, int count, OptionDetail... optionDetails) {
        // 회원
        Member member = memberRepository.findById(memberId).orElseThrow();
        // 제품 선택´
        Item selectItem = itemRepository.findById(itemId).orElseThrow();
        // 상품 생성
        CartItem cartItem = CartItem.createCartItem(selectItem, count, optionDetails);
        // 장바구니가 기존에 없는 경우 바로 생성
        Optional<Cart> findCart = cartRepository.findCart(member.getId());
        Cart cart = new Cart();
        if(findCart.isPresent()) {
            cart = findCart.get();
            // 장바구니가 있는 경우 매장을 비교한다.
//            Store store = cart.getCartItemList().stream()
//                    .map(CartItem::getItem)
//                    .map(Item::getStore)
//                    .findFirst()
//                    .orElseThrow();
            // 동일한 매장인지 비교
            if (cart.getStoreId().equals(selectItem.getStore().getId())) {
                // 비즈니스 로직 수행
                // 동일한 제품이 있는지?
                List<CartItem> dupleItemList = cart.getCartItemList().stream()
                        .filter(findCartItem -> findCartItem.getItem().getId().equals(selectItem.getId())) // 선택한 상품 아이디 확인
                        .collect(Collectors.toList());

                // 선택한 상품이 장바구니에 있는 경우
                if(!dupleItemList.isEmpty()) {
                    // 옵션이 없는 상품이 존재할 경우 카운트 추가
                    if(optionDetails.length == 0) {
                        for (CartItem item : dupleItemList) {
                            if(item.getCartOptions().isEmpty()) {
                                item.addCartItemCount(cartItem.getCount(), cartItem.getCartItemPrice());
                            }
                        }
                    } else {
                        boolean isAllMatch = false;
                        // 동일한 옵션이 존재하는지 확인
                        for (CartItem item : dupleItemList) {
//                            List<Long> cartOptionIds = item.getCartOptions().stream()
//                                    .map(cartOption -> cartOption.getOptionDetail().getId())
//                                    .sorted()
//                                    .collect(Collectors.toList());
//
//                            List<Long> sortedOptionDetailIds = Arrays.stream(optionDetails)
//                                    .map(OptionDetail::getId)
//                                    .sorted()
//                                    .collect(Collectors.toList());
                            /**
                             * 편의 메소드를 구현하여 코드 가독성 개선 및 도메인 응집도 증대
                             * cartItem.hasSameOptions() !!
                             */
                            isAllMatch = item.hasSameOptions(optionDetails);
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
            } else {
                return CartCommonResponse.storeChangeConfirm(cart);
            }
        } else {
            cartRepository.save(Cart.createCart(member, cartItem, selectItem.getStore().getId()));
        }

        return CartCommonResponse.addSuccess(cart);
    }
}

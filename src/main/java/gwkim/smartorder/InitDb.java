package gwkim.smartorder;

import gwkim.smartorder.item.domain.Item;
import gwkim.smartorder.item.repository.ItemRepository;
import gwkim.smartorder.member.domain.Member;
import gwkim.smartorder.member.repository.MemberRepository;
import gwkim.smartorder.option.domain.Option;
import gwkim.smartorder.option.domain.OptionDetail;
import gwkim.smartorder.option.repository.OptionRepository;
import gwkim.smartorder.store.domain.Store;
import gwkim.smartorder.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class InitDb {
    private final InitService initService;

    @PostConstruct
    public void init() {
        initService.dbInit1();
    }

    @Component
    @Transactional
    @RequiredArgsConstructor
    static class InitService {
        private final StoreRepository storeRepository;
        private final ItemRepository itemRepository;
        private final OptionRepository optionRepository;
        private final MemberRepository memberRepository;

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

            optionRepository.save(Option.createOption(saveItem1, "사이즈", "Y", 1,
                    1, 1, optionDetailList1.toArray(new OptionDetail[optionDetailList1.size()])));

            List<OptionDetail> optionDetailList2 = new ArrayList<>();
            optionDetailList2.add(getOptionDetail("조금", 0, 1));
            optionDetailList2.add(getOptionDetail("많이", 0, 2));

            optionRepository.save(Option.createOption(saveItem1, "얼음", "N", 0,
                    0, 2, optionDetailList2.toArray(new OptionDetail[optionDetailList2.size()])));


            Item saveItem2 = itemRepository.save(createItem(saveStore, "카페라떼", 4500, "P", "1001", "Y"));

            List<OptionDetail> optionDetailList3 = new ArrayList<>();
            optionDetailList1.add(getOptionDetail("소", 0, 1));
            optionDetailList1.add(getOptionDetail("중", 1500, 2));
            optionDetailList1.add(getOptionDetail("대", 2500, 3));

            optionRepository.save(Option.createOption(saveItem2, "사이즈", "Y", 1,
                    1, 1, optionDetailList1.toArray(new OptionDetail[optionDetailList1.size()])));

            List<OptionDetail> optionDetailList4 = new ArrayList<>();
            optionDetailList2.add(getOptionDetail("조금", 0, 1));
            optionDetailList2.add(getOptionDetail("많이", 0, 2));

            optionRepository.save(Option.createOption(saveItem2, "얼음", "N", 0,
                    0, 2, optionDetailList4.toArray(new OptionDetail[optionDetailList4.size()])));

            List<OptionDetail> optionDetailList5 = new ArrayList<>();
            optionDetailList2.add(getOptionDetail("2샷", 500, 1));
            optionDetailList2.add(getOptionDetail("3샷", 1000, 2));
            optionDetailList2.add(getOptionDetail("4샷", 1500, 3));

            optionRepository.save(Option.createOption(saveItem2, "샷 추가", "N", 0,
                    0, 3, optionDetailList2.toArray(new OptionDetail[optionDetailList5.size()])));


            Item saveItem3 = itemRepository.save(createItem(saveStore, "망고 에이드", 5500, "R", "1001", "Y"));

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
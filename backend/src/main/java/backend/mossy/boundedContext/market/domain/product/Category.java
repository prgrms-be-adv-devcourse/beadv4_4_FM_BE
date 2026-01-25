package backend.mossy.boundedContext.market.domain.product;

import backend.mossy.global.jpa.entity.BaseIdAndTime;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "MARKET_CATEGORY")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AttributeOverride(name = "id", column = @Column(name = "category_id"))
public class Category extends BaseIdAndTime {

    @Column(nullable = false, length = 100)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<Category> children = new ArrayList<>();

    @Column(name = "depth", nullable = false) // level 대신 depth 추천
    private Integer depth;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0; // 기본값 설정

    public Category(String name, Category parent, Integer sortOrder) {
        this.name = name;
        this.parent = parent;
        this.depth = (parent == null) ? 1 : parent.getDepth() + 1;
        this.sortOrder = (sortOrder == null) ? 0 : sortOrder;

        if (parent != null) {
            parent.getChildren().add(this);
        }
    }
}

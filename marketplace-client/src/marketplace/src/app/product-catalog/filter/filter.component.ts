import {ChangeDetectorRef, Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import { Subscription } from 'rxjs';
import { Filter } from 'src/app/_models/products/filter';
import { Product } from 'src/app/_models/products/product';
import { ProductDto } from 'src/app/_models/products/productDto';
import { ProductService } from 'src/app/_services/product.service';
import { Options } from '@angular-slider/ngx-slider';

@Component({
  selector: 'app-filter',
  templateUrl: './filter.component.html',
  styleUrls: ['./filter.component.scss'],
})
export class FilterComponent implements OnInit {
  categories: string[] = [];
  filters!: Filter;

  products: Product[] = [];
  subscription!: Subscription;
  categorySubscription!: Subscription;
  rangeSubscription!: Subscription;
  minPrice: number = 0;
  maxPrice: number = 99999;
  options: Options = {
    floor: 0,
    ceil: 99999,
    showTicks: false,
  };
  @Input() sort: string | null = null;
  @Output() sortChange = new EventEmitter<string>();
  @Input() direction: string | null = null;
  @Output() directionChange = new EventEmitter<string>();
  @Output() results: EventEmitter<any> = new EventEmitter<any>();

  constructor(private service: ProductService,
              private cdref: ChangeDetectorRef) {}

  ngOnInit(): void {
    this.getCategories();
    this.filters = this.getFilter();
    this.sort=this.filters.sort;
    this.direction=this.filters.direction;
    this.sortChange.emit(this.sort);
    this.directionChange.emit(this.direction);
    this.minPrice=this.filters.minPrice;
    this.maxPrice=this.filters.maxPrice;
    this.getPriceRange();
    this.filter(this.filters, true);
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
    this.categorySubscription.unsubscribe();
    this.rangeSubscription.unsubscribe();
  }

  ngOnChanges():void{
    if(this.filters) {
      if(this.sort) this.filters.sort = this.sort;
      if(this.direction) this.filters.direction = this.direction;
      this.filter(this.filters, false);
    }
  }

  private filter(filter: Filter, init: boolean, category?: boolean) {
    this.subscription = this.service
      .getFilteredProducts(filter, init)
      .subscribe((results: ProductDto) => {
        if(category){
          this.getPriceRange();
        }
        this.products = results.result_set;
        this.results.emit();
      });
  }

  chooseCategory(category: string) {
    console.log(category);
    this.filters.category = category;
    this.filter(this.filters, false, true);
  }

  setPrice():void {
    this.filters.maxPrice=this.maxPrice;
    this.filters.minPrice=this.minPrice;
    this.filter(this.filters, false);
  }

  private getFilter(): Filter {
    return this.service.getFilter();
  }

  private getCategories() {
    this.categorySubscription = this.service
      .getCategories()
      .subscribe((results: string[]) => {
        this.categories = results;
        this.categories.unshift('all');
      });
  }

  private getPriceRange() {
    this.rangeSubscription = this.service
      .getPriceRange(this.filters.category)
      .subscribe((results: number[]) => {
        this.options = {
          floor: Math.floor(results[0]),
          ceil: Math.ceil(results[1]),
          showTicks: false,
        };
      });
  }

  getProducts(): Product[] {
    return this.products;
  }

  isPriceAsc(): boolean {
    return this.filters.sort === 'price' && this.filters.direction === 'ASC';
  }

  isPriceDesc(): boolean {
    return this.filters.sort === 'price' && this.filters.direction === 'DESC';
  }

  isNameAsc(): boolean {
    return this.filters.sort === 'name' && this.filters.direction === 'ASC';
  }

  isNameDesc(): boolean {
    return this.filters.sort === 'name' && this.filters.direction === 'DESC';
  }
}

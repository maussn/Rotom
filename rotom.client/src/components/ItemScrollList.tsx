import type { Item } from "../types";
import ItemContainer from "./ItemContainer";

type Props = {
items: Item[];
};


export default function ItemScrollList({ items }: Props) {
  return (
    <div className="items-list">
      {items.map((item) => (
        <ItemContainer key={item.id} item={item} />
      ))}
    </div>
  );
}
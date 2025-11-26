import type { Item } from "../types";

type Props = {
  item: Item;
};

export default function ItemContainer({ item }: Props) {
    return (
    <div>
      <h2 className="item-title">{item.name}</h2>
      <p className="item-desc">{item.description ?? "No description"}</p>
    </div>
  )
}

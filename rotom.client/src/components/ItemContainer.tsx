import type { Item } from "../types";

type Props = {
  item: Item;
};

export default function ItemContainer({ item }: Props) {
    return (
    <article className="item-container">
      <h2 className="item-title">{item.name}</h2>
      <p className="item-desc">{item.description ?? "No description"}</p>
    </article>
  )
}

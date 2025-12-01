import type { Item } from "../types";

type Props = {
  item: Item;
};

export default function ItemContainer({ item }: Props) {
    const handleLoanRequest = () => {
      
    }
    return (
    <article className="item-container">
      <div className="item-header">
        <h2 className="item-title">{item.name}</h2>
        <button onClick={handleLoanRequest}>Loan</button>
      </div>
      <p className="item-desc">{item.description ?? "No description"}</p>
    </article>
  )
}

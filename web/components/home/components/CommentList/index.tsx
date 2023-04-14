import React from 'react';
import styles from './index.module.scss';
import WriteComment from '../WriteComment';
import { useFetchCommentsQuery } from '@/apis';
import CommentItem from './CommentItem';

interface Props {
  postId: number;
  items: any[];
}

export default function CommentList(props: Props) {
  const { postId, items = [] } = props;
  return (
    <div>
      <div className={styles.commentArea}>
        {/* <WriteComment postId={postId}></WriteComment> */}
        {items.map(
          (item: {
            createdAt: string;
            id: number;
            author: {
              about: string;
              image: string;
              username: string;
            };
            content: string;
          }) => {
            return <CommentItem key={item.id} postId={postId} item={item}></CommentItem>;
          }
        )}
      </div>
    </div>
  );
}

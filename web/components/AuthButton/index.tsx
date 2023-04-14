import { Button, ButtonProps } from 'antd';
import React from 'react';
import { useAppDispatch, useAppSelector } from '../../hooks';
import { showLoginModal } from '../../store/app';

export default function AuthButton(props: ButtonProps) {
  // const user = useAppSelector((state) => state.app.user);
  // const dispatch = useAppDispatch();
  return (
    <Button
      {...props}
      size="small"
      style={{ fontSize: 12 }}
      onClick={(e) => {
        // if (!user) {
        //   return dispatch(showLoginModal(true));
        // }
        return props?.onClick?.(e);
      }}
    ></Button>
  );
}

import { IUser } from 'app/entities/user/user.model';

export interface IApplicationUser {
  id: number;
  nickName?: string | null;
  user?: Pick<IUser, 'id' | 'login'> | null;
}

export type NewApplicationUser = Omit<IApplicationUser, 'id'> & { id: null };

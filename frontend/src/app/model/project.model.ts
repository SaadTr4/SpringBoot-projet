import { User } from './user.model';

export interface Project {
  id?: number;
  name: string;
  description?: string;
  projectManagerName?: string;
  projectManagerId?: number;
  users?: User[];
  status?: string;
}

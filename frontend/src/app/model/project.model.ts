import { User } from './user.model';

export interface Project {
  id?: number;
  name: string;
  description?: string;
  projectManager?: User;
  users?: User[];
  status?: 'IN_PROGRESS' | 'COMPLETED' | 'CANCELLED' | 'PLANNED';
}
